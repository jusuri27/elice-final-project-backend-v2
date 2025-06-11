# Excel 처리 및 대용량 업로드 관련 리팩토링

## 1. 엑셀 데이터 가독성 향상

- **레이아웃 조정**
  - 첫 번째 열(A열)과 첫 번째 행(1행)을 공백으로 비움
  - 2행 2열(B2 셀)부터 데이터 표시

- **시각적 강조**
  - 헤더(제목 행)에 색상 적용하여 강조
  - 바디 영역에는 셀 테두리 추가로 구분감 확보

| 변경 전 | 변경 후 |
|--------|--------|
| <img src="https://github.com/user-attachments/assets/e4ef5f03-a10e-4bf2-8f2d-d0f75e41df77" width="100%" height=500/> | <img src="https://github.com/user-attachments/assets/eb29a935-d1f5-41df-94c6-09cd9b4939be" width="100%" height=500/> |
| 변경 전 | 변경 후 |

| 변경 전 | 변경 후 |
|--------|--------|
| <img src="https://github.com/user-attachments/assets/169f1291-28ae-49b6-9186-9a6a09d12f7d" width="100%" height=500/> | <img src="https://github.com/user-attachments/assets/e3b0b92b-91a8-40c4-823d-b7c6ca2e8936" width="100%" height=500/> |



---

## 2. 엑셀 파싱 실패 이슈 분석 및 해결

### 📌 에러 유형
```
org.apache.poi.util.RecordFormatException: Tried to read data but the maximum length for this record type is 100,000,000.
```

### ⚠️ 발생 원인
- Apache POI는 기본적으로 **1개 레코드당 최대 100MB**만 허용
- `sheet1.xml`의 압축 해제 크기가 246MB로, 100MB 초과
  - 실제 로그 예시:
    ```
    Entry: xl/worksheets/sheet1.xml, Compressed Size: 22.05 MB, Uncompressed Size: 246.73 MB
    ```

### ✅ 해결 방법
- Apache POI 설정값 변경: `IOUtils.setByteArrayMaxOverride()`를 이용해 레코드 허용 크기 확대

```java
public <T> List<T> parseExcelToObject(MultipartFile file, Class<T> clazz) throws IOException {
    IOUtils.setByteArrayMaxOverride(300_000_000); // 레코드 최대 크기 300MB로 설정
    Workbook workbook = WorkbookFactory.create(file.getInputStream());
    Sheet sheet = workbook.getSheetAt(0);

    parseHeader(sheet, clazz);
    return parseBody(sheet, clazz);
}
```

## 3. 엑셀 파싱 문제 리팩토링

### 📌 문제 상황

- 100만 건이 포함된 대용량 엑셀 파일 업로드 시 `OutOfMemoryError` 발생

### 📈 테스트로 확인한 메모리 사용량

| 데이터 건수 | Max Memory | Total Memory | Free Memory | API 사용 메모리 | 수행 시간 |
|-------------|------------|---------------|--------------|------------------|-------------|
| 10만 건     | 2048MB     | 512MB          | 458MB         | 403MB             | 8초         |
| 30만 건     | 2048MB     | 512MB          | 461MB         | 1227MB            | 14초        |
| 100만 건    | 2048MB     | 512MB          | ???MB         | 예상 4000MB 이상  | 예상 40초   |

### ⚠️ 원인 분석

- 기존 Excel 파싱 방식은 **DOM 기반 (WorkbookFactory.create)**
  - 전체 엑셀 파일을 한 번에 메모리에 로드
  - 구조 접근은 쉽지만, 대용량 파일 처리에 매우 비효율적

### 🚨 발생 에러
```
java.lang.OutOfMemoryError: Java heap space
```


### ✅ 해결 방안: SAX(Streaming) 방식 전환

| 항목             | DOM 방식 (기존)                   | SAX 기반 Streaming 방식 (개선)        |
|------------------|-----------------------------------|----------------------------------------|
| 사용 라이브러리   | Apache POI                        | com.monitorjbl:xlsx-streamer           |
| 처리 방식         | 전체 엑셀 메모리에 로드           | 필요한 데이터만 순차적으로 읽음        |
| 메모리 사용량     | 매우 높음                         | 매우 낮음                              |
| 대용량 처리       | 비효율적 (수만 건 이상에서 위험) | 효율적 (수십만 ~ 백만 건 이상 가능)     |
| 코드 구조         | 접근 편리                         | 파싱 로직 직접 구현 필요               |

### 🔧 적용 예시 (SAX 기반)

```java
try (InputStream is = file.getInputStream()) {
    StreamingReader reader = StreamingReader.builder()
        .rowCacheSize(100)
        .bufferSize(4096)
        .open(is);

    Sheet sheet = reader.getSheetAt(0);
    for (Row row : sheet) {
        // 데이터 처리 로직
    }
}




