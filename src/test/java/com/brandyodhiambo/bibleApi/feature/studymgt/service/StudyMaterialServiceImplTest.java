package com.brandyodhiambo.bibleApi.feature.studymgt.service;

import com.brandyodhiambo.bibleApi.exception.ResourceNotFoundException;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.GroupType;
import com.brandyodhiambo.bibleApi.feature.groupmgt.repository.GroupRepository;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.StudyMaterial;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.dto.StudyMaterialResponse;
import com.brandyodhiambo.bibleApi.feature.studymgt.repository.StudyMaterialRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Role;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.RoleName;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudyMaterialServiceImplTest {

    @Mock
    private StudyMaterialRepository studyMaterialRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StudyMaterialServiceImpl studyMaterialService;

    private Users leader;
    private Users member;
    private Group group;
    private StudyMaterial studyMaterial;
    private MultipartFile file;

    // Helper method to set the id field of Users using reflection
    private void setUserId(Users user, Long id) {
        try {
            Field idField = Users.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set user id", e);
        }
    }

    @BeforeEach
    void setUp() {
        // Create roles
        Role leaderRole = new Role();
        leaderRole.setName(RoleName.ROLE_LEADER);

        Role memberRole = new Role();
        memberRole.setName(RoleName.ROLE_MEMBER);

        // Create users
        leader = new Users();
        setUserId(leader, 1L);
        leader.setUsername("leader");
        leader.setFirstName("Leader");
        leader.setLastName("User");
        leader.setEmail("leader@example.com");
        leader.setRole(Set.of(leaderRole));

        member = new Users();
        setUserId(member, 2L);
        member.setUsername("member");
        member.setFirstName("Member");
        member.setLastName("User");
        member.setEmail("member@example.com");
        member.setRole(Set.of(memberRole));

        // Create group
        group = new Group();
        group.setId(1L);
        group.setName("Test Group");
        group.setDescription("Test Group Description");
        group.setType(GroupType.VIRTUAL);
        group.setLeader(leader);
        group.setMembers(new HashSet<>(Arrays.asList(leader, member)));
        group.setCreatedAt(LocalDate.now());
        group.setUpdatedAt(LocalDate.now());

        // Create study material
        studyMaterial = StudyMaterial.builder()
                .id(1L)
                .title("Test Material")
                .description("Test Material Description")
                .fileName("test.pdf")
                .fileType("application/pdf")
                .fileSize(1024L)
                .fileData("test data".getBytes())
                .group(group)
                .uploadedBy(leader)
                .keywords("test, material")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Create mock file
        file = new MockMultipartFile(
                "test.pdf",
                "test.pdf",
                "application/pdf",
                "test data".getBytes()
        );
    }

    @Test
    void uploadStudyMaterial_AsLeader_Success() throws IOException {
        // Arrange
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.getUserByName("leader")).thenReturn(leader);
        when(studyMaterialRepository.save(any(StudyMaterial.class))).thenReturn(studyMaterial);

        // Act
        StudyMaterialResponse response = studyMaterialService.uploadStudyMaterial(
                1L, "Test Material", "Test Material Description", "test, material", file, "leader");

        // Assert
        assertNotNull(response);
        assertEquals("Test Material", response.getTitle());
        assertEquals("Test Material Description", response.getDescription());
        assertEquals("test.pdf", response.getFileName());
        assertEquals("application/pdf", response.getFileType());
        assertEquals(1024L, response.getFileSize());
        assertEquals(1L, response.getGroupId());
        assertEquals("Test Group", response.getGroupName());
        assertEquals("leader", response.getUploadedBy().getUsername());
        assertEquals("test, material", response.getKeywords());

        verify(studyMaterialRepository, times(1)).save(any(StudyMaterial.class));
    }

    @Test
    void uploadStudyMaterial_AsMember_ThrowsAccessDeniedException() {
        // Arrange
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.getUserByName("member")).thenReturn(member);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            studyMaterialService.uploadStudyMaterial(
                    1L, "Test Material", "Test Material Description", "test, material", file, "member");
        });

        verify(studyMaterialRepository, never()).save(any(StudyMaterial.class));
    }

    @Test
    void getStudyMaterial_ExistingId_ReturnsStudyMaterial() {
        // Arrange
        when(studyMaterialRepository.findById(1L)).thenReturn(Optional.of(studyMaterial));

        // Act
        StudyMaterialResponse response = studyMaterialService.getStudyMaterial(1L);

        // Assert
        assertNotNull(response);
        assertEquals("Test Material", response.getTitle());
        assertEquals("Test Material Description", response.getDescription());
        assertEquals("test.pdf", response.getFileName());

        verify(studyMaterialRepository, times(1)).findById(1L);
    }

    @Test
    void getStudyMaterial_NonExistingId_ThrowsResourceNotFoundException() {
        // Arrange
        when(studyMaterialRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studyMaterialService.getStudyMaterial(999L);
        });

        verify(studyMaterialRepository, times(1)).findById(999L);
    }

    @Test
    void getStudyMaterialsByGroup_ExistingGroupId_ReturnsStudyMaterials() {
        // Arrange
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(studyMaterialRepository.findByGroup(group)).thenReturn(List.of(studyMaterial));

        // Act
        List<StudyMaterialResponse> response = studyMaterialService.getStudyMaterialsByGroup(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Test Material", response.get(0).getTitle());

        verify(groupRepository, times(1)).findById(1L);
        verify(studyMaterialRepository, times(1)).findByGroup(group);
    }

    @Test
    void searchStudyMaterials_WithTerm_ReturnsMatchingStudyMaterials() {
        // Arrange
        when(studyMaterialRepository.searchByTerm("test")).thenReturn(List.of(studyMaterial));

        // Act
        List<StudyMaterialResponse> response = studyMaterialService.searchStudyMaterials("test");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Test Material", response.get(0).getTitle());

        verify(studyMaterialRepository, times(1)).searchByTerm("test");
    }

    @Test
    void deleteStudyMaterial_AsLeader_Success() {
        // Arrange
        when(studyMaterialRepository.findById(1L)).thenReturn(Optional.of(studyMaterial));
        when(userRepository.getUserByName("leader")).thenReturn(leader);

        // Act
        studyMaterialService.deleteStudyMaterial(1L, "leader");

        // Assert
        verify(studyMaterialRepository, times(1)).findById(1L);
        verify(studyMaterialRepository, times(1)).delete(studyMaterial);
    }

    @Test
    void deleteStudyMaterial_AsMember_ThrowsAccessDeniedException() {
        // Arrange
        when(studyMaterialRepository.findById(1L)).thenReturn(Optional.of(studyMaterial));
        when(userRepository.getUserByName("member")).thenReturn(member);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            studyMaterialService.deleteStudyMaterial(1L, "member");
        });

        verify(studyMaterialRepository, times(1)).findById(1L);
        verify(studyMaterialRepository, never()).delete(any(StudyMaterial.class));
    }
}
