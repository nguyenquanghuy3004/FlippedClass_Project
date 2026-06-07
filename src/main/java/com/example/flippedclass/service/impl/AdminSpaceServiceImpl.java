package com.example.flippedclass.service.impl;

import com.example.flippedclass.dto.LearningSpaceDetailDto;
import com.example.flippedclass.dto.LearningSpaceDto;
import com.example.flippedclass.entity.LearningSpace;
import com.example.flippedclass.enums.LearningPathStatus;
import com.example.flippedclass.enums.LearningSpaceStatus;
import com.example.flippedclass.repository.LearningSpaceRepository;
import com.example.flippedclass.security.SpaceSecurityEvaluator;
import com.example.flippedclass.service.AdminSpaceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminSpaceServiceImpl implements AdminSpaceService {

    @Autowired
    private LearningSpaceRepository spaceRepository;


    @Override
    public Page<LearningSpaceDto> getAllPages(String keyword, LearningSpaceStatus status, Pageable pageable){
        Page<LearningSpace> spaces = spaceRepository.searchAndFilterSpaces(keyword,status,pageable);
                return  spaces.map(this :: convertToDto);
    }

    @Override
    public LearningSpaceDetailDto getSpaceDetail(Long spaceId){
        LearningSpace space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("Space not found"));


        LearningSpaceDetailDto detailDto = new LearningSpaceDetailDto();
        BeanUtils.copyProperties(space, detailDto);

        detailDto.setOwnerEmail(space.getOwner().getEmail());

        detailDto.setMemberCount(10);
        detailDto.setLearningPathCount(5);
        detailDto.setLessonCount(20);

        return detailDto;
    }

    @Override
    public void updateSpaceStatus(Long spaceId, LearningSpaceStatus newStatus){
        LearningSpace space = spaceRepository.findById(spaceId).orElseThrow(() -> new IllegalArgumentException("Space not found"));
        space.setStatus(newStatus);
        spaceRepository.save(space);
    }

    private LearningSpaceDto convertToDto(LearningSpace space) {
        LearningSpaceDto dto = new LearningSpaceDto();
        BeanUtils.copyProperties(space, dto);
        dto.setOwnerEmail(space.getOwner().getEmail());
        return dto;
    }
}
