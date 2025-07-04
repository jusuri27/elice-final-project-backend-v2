# Excel 처리 및 대용량 업로드 관련 리팩토링

## 1. 엑셀 데이터 가독성 향상

- **레이아웃 조정**
  - 첫 번째 열(A열)과 첫 번째 행(1행)을 공백으로 비움
  - 2행 2열(B2 셀)부터 데이터 표시

- **시각적 강조**
  - 헤더(제목 행)에 색상 적용하여 강조
  - 바디 영역에는 셀 테두리 추가로 구분감 확보
    
### ✅ 개인별 연 급여 통계
| 변경 전 | 변경 후 |
|--------|--------|
| <img src="https://github.com/user-attachments/assets/169f1291-28ae-49b6-9186-9a6a09d12f7d" width="100%" height=500/> | <img src="https://github.com/user-attachments/assets/e3b0b92b-91a8-40c4-823d-b7c6ca2e8936" width="100%" height=500/> |

### ✅ 부서별 월 급여 통계
| 변경 전 | 변경 후 |
|--------|--------|
| <img src="https://github.com/user-attachments/assets/e4ef5f03-a10e-4bf2-8f2d-d0f75e41df77" width="100%" height=500/> | <img src="https://github.com/user-attachments/assets/eb29a935-d1f5-41df-94c6-09cd9b4939be" width="100%" height=500/> |
| 변경 전 | 변경 후 |

---
## 2. SAX 방식 도입을 통한 엑셀 파싱 리팩토링
✅ DOM 방식(변경전)
| 데이터 건수 | Max Memory | Total Memory | Free Memory | API 사용 메모리       | 수행 시간      |
| ------ | ---------- | ------------ | ----------- | ---------------- | ---------- |
| 10만 건  | 2048MB     | 512MB        | 458MB       | 403MB            | 8초         |
| 30만 건  | 2048MB     | 512MB        | 461MB       | 1227MB           | 14초        |
| 100만 건 | 2048MB     | 512MB        | ???MB       | **예상 4000MB 이상** | **예상 40초** |


✅ SAX 방식(변경후)
| 데이터 건수 | Max Memory | Total Memory | Free Memory | API 사용 메모리   | 수행 시간   |
| ------ | ---------- | ------------ | ----------- | ------------ | ------- |
| 10만 건  | 2048MB     | 512MB        | 458MB       | 12MB         | 7초      |
| 30만 건  | 2048MB     | 512MB        | 461MB       | 35MB         | 12초     |
| 100만 건 | 2048MB     | 512MB        | 460MB       | **예상 113MB** | **27초** |


💡 메모리 사용 비교
| 데이터 건수 | DOM 사용량    | SAX 사용량   | 메모리 절감률           |
| ------ | ---------- | --------- | ----------------- |
| 10만 건  | 403MB      | 12MB      | 약 **97% 절감**      |
| 30만 건  | 1227MB     | 35MB      | 약 **97.2% 절감**    |
| 100만 건 | 4000MB(예상) | 113MB(예상) | 약 **97.2% 이상 절감** |


---
## 3. 대용량 데이터 처리 성능 개선을 위한 저장 로직 리팩토링


✅ saveAll() vs foreach 성능 비교
| saveAll()         | 데이터 건수    | 수행 시간 (초) |
| ----------- | --------- | --------- |
| `saveAll()` | 100,000   | 37        |
| `saveAll()` | 300,000   | 103       |
| `saveAll()` | 1,000,000 | 325       |

| foreach         | 데이터 건수    | 수행 시간 (초) |
| ----------- | --------- | --------- |
| `foreach`  | 100,000   | 7         |
| `foreach`  | 300,000   | 22        |
| `foreach`  | 1,000,000 | 68        |

✅ foreach 10만 건씩 반복 처리 (엑셀 100만건 기준)
| 수행 횟수  | Total Memory (MB) | Free Memory (MB) | Used Memory (MB) | 실행 시간 (초) |
| ------ | ----------------- | ---------------- | ---------------- | --------- |
| 1      | 1119.000          | 446.207          | 672.793          | 8.36      |
| 2      | 1206.000          | 628.121          | 577.879          | 6.65      |
| 3      | 1402.000          | 805.998          | 596.002          | 6.55      |
| 4      | 1402.000          | 704.200          | 697.800          | 6.46      |
| 5      | 1661.000          | 646.314          | 1014.686         | 6.43      |
| 6      | 1677.000          | 563.840          | 1113.160         | 6.52      |
| 7      | 1677.000          | 679.430          | 997.570          | 6.76      |
| 8      | 1826.000          | 1163.502         | 662.498          | 6.72      |
| 9      | 1913.000          | 953.057          | 959.943          | 6.47      |
| 10     | 1967.000          | 970.107          | 996.893          | 6.63      |
| **총합** | -                 | -                | -                | **68.53** |

---
## 트러블 슈팅
### 100MB 초과로 인한 엑셀 파싱 실패 -> [블로그 정리](https://rnwns2.tistory.com/160)
### 대용량 엑셀 파일 파싱시 메모리 부족 -> [블로그 정리](https://rnwns2.tistory.com/161)
### 대용량 데이터 저장시 메모리 부족 -> [블로그 정리](https://rnwns2.tistory.com/162)



