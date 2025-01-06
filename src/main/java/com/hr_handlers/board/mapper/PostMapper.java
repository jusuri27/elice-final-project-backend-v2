package com.hr_handlers.board.mapper;

import com.hr_handlers.board.dto.PostDetailResponseDto;
import com.hr_handlers.board.dto.PostResponseDto;
import com.hr_handlers.board.entity.HashTag;
import com.hr_handlers.board.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "employee.name", target = "employeeName")
    @Mapping(target = "hashtagContent", qualifiedByName = "mapHashTagsToContent")
    PostResponseDto toPostResponseDto(Post post);

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "employee.name", target = "employeeName")
    @Mapping(target = "hashtagContent", qualifiedByName = "mapHashTagsToContent")
    PostDetailResponseDto toPostDetailResponseDto(Post post);

    @Named("mapHashTagsToContent")
    default List<String> mapHashTagsToContent(List<HashTag> hashTags) {
        if (hashTags == null) {
            return new ArrayList<>();
        }
        return hashTags.stream()
                .map(HashTag::getHashtagContent)
                .toList();
    }
}
