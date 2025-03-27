package com.brandyodhiambo.bibleApi.feature.studymgt.service;

import com.brandyodhiambo.bibleApi.exception.ResourceNotFoundException;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.GroupType;
import com.brandyodhiambo.bibleApi.feature.groupmgt.repository.GroupRepository;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.ReadingPlan;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.dto.ReadingPlanResponse;
import com.brandyodhiambo.bibleApi.feature.studymgt.repository.ReadingPlanRepository;
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
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReadingPlanServiceImplTest {

    @Mock
    private ReadingPlanRepository readingPlanRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReadingPlanServiceImpl readingPlanService;

    private Users leader;
    private Users member;
    private Group group;
    private ReadingPlan readingPlan;

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
        
        // Create reading plan
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);
        
        readingPlan = ReadingPlan.builder()
                .id(1L)
                .title("Test Reading Plan")
                .description("Test Reading Plan Description")
                .bibleReferences("John 3:16-21, Romans 8:1-11")
                .startDate(startDate)
                .endDate(endDate)
                .group(group)
                .createdBy(leader)
                .topics("faith, love, salvation")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createReadingPlan_AsLeader_Success() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);
        
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.getUserByName("leader")).thenReturn(leader);
        when(readingPlanRepository.save(any(ReadingPlan.class))).thenReturn(readingPlan);
        
        // Act
        ReadingPlanResponse response = readingPlanService.createReadingPlan(
                1L, "Test Reading Plan", "Test Reading Plan Description", 
                "John 3:16-21, Romans 8:1-11", startDate, endDate, 
                "faith, love, salvation", "leader");
        
        // Assert
        assertNotNull(response);
        assertEquals("Test Reading Plan", response.getTitle());
        assertEquals("Test Reading Plan Description", response.getDescription());
        assertEquals("John 3:16-21, Romans 8:1-11", response.getBibleReferences());
        assertEquals(startDate, response.getStartDate());
        assertEquals(endDate, response.getEndDate());
        assertEquals(1L, response.getGroupId());
        assertEquals("Test Group", response.getGroupName());
        assertEquals("leader", response.getCreatedBy().getUsername());
        assertEquals("faith, love, salvation", response.getTopics());
        
        verify(readingPlanRepository, times(1)).save(any(ReadingPlan.class));
    }

    @Test
    void createReadingPlan_AsMember_ThrowsAccessDeniedException() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);
        
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.getUserByName("member")).thenReturn(member);
        
        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            readingPlanService.createReadingPlan(
                    1L, "Test Reading Plan", "Test Reading Plan Description", 
                    "John 3:16-21, Romans 8:1-11", startDate, endDate, 
                    "faith, love, salvation", "member");
        });
        
        verify(readingPlanRepository, never()).save(any(ReadingPlan.class));
    }

    @Test
    void getReadingPlan_ExistingId_ReturnsReadingPlan() {
        // Arrange
        when(readingPlanRepository.findById(1L)).thenReturn(Optional.of(readingPlan));
        
        // Act
        ReadingPlanResponse response = readingPlanService.getReadingPlan(1L);
        
        // Assert
        assertNotNull(response);
        assertEquals("Test Reading Plan", response.getTitle());
        assertEquals("Test Reading Plan Description", response.getDescription());
        assertEquals("John 3:16-21, Romans 8:1-11", response.getBibleReferences());
        
        verify(readingPlanRepository, times(1)).findById(1L);
    }

    @Test
    void getReadingPlan_NonExistingId_ThrowsResourceNotFoundException() {
        // Arrange
        when(readingPlanRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            readingPlanService.getReadingPlan(999L);
        });
        
        verify(readingPlanRepository, times(1)).findById(999L);
    }

    @Test
    void getReadingPlansByGroup_ExistingGroupId_ReturnsReadingPlans() {
        // Arrange
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(readingPlanRepository.findByGroup(group)).thenReturn(List.of(readingPlan));
        
        // Act
        List<ReadingPlanResponse> response = readingPlanService.getReadingPlansByGroup(1L);
        
        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Test Reading Plan", response.get(0).getTitle());
        
        verify(groupRepository, times(1)).findById(1L);
        verify(readingPlanRepository, times(1)).findByGroup(group);
    }

    @Test
    void searchReadingPlans_WithTerm_ReturnsMatchingReadingPlans() {
        // Arrange
        when(readingPlanRepository.searchByTerm("faith")).thenReturn(List.of(readingPlan));
        
        // Act
        List<ReadingPlanResponse> response = readingPlanService.searchReadingPlans("faith");
        
        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Test Reading Plan", response.get(0).getTitle());
        
        verify(readingPlanRepository, times(1)).searchByTerm("faith");
    }

    @Test
    void deleteReadingPlan_AsLeader_Success() {
        // Arrange
        when(readingPlanRepository.findById(1L)).thenReturn(Optional.of(readingPlan));
        when(userRepository.getUserByName("leader")).thenReturn(leader);
        
        // Act
        readingPlanService.deleteReadingPlan(1L, "leader");
        
        // Assert
        verify(readingPlanRepository, times(1)).findById(1L);
        verify(readingPlanRepository, times(1)).delete(readingPlan);
    }

    @Test
    void deleteReadingPlan_AsMember_ThrowsAccessDeniedException() {
        // Arrange
        when(readingPlanRepository.findById(1L)).thenReturn(Optional.of(readingPlan));
        when(userRepository.getUserByName("member")).thenReturn(member);
        
        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            readingPlanService.deleteReadingPlan(1L, "member");
        });
        
        verify(readingPlanRepository, times(1)).findById(1L);
        verify(readingPlanRepository, never()).delete(any(ReadingPlan.class));
    }
}