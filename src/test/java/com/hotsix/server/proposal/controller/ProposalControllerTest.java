package com.hotsix.server.proposal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.proposal.dto.ProposalRequestBody;
import com.hotsix.server.proposal.dto.ProposalRequestDto;
import com.hotsix.server.proposal.dto.ProposalResponseDto;
import com.hotsix.server.proposal.dto.ProposalStatusRequestBody;
import com.hotsix.server.proposal.entity.Proposal;
import com.hotsix.server.proposal.entity.ProposalStatus;
import com.hotsix.server.proposal.service.ProposalService;
import com.hotsix.server.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
class ProposalControllerTest {

    private MockMvc mockMvc;
    private ProposalService proposalService;
    private Rq rq;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        proposalService = Mockito.mock(ProposalService.class);
        rq = Mockito.mock(Rq.class);
        objectMapper = new ObjectMapper();

        ProposalController controller = new ProposalController(proposalService, rq);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    // 공통 더미 Proposal 생성 메서드
    private Proposal createDummyProposal(Long id, String description, int amount) {
        Proposal mockProposal = Mockito.mock(Proposal.class);

        // Project mock
        Project mockProject = Mockito.mock(Project.class);
        when(mockProject.getProjectId()).thenReturn(100L);

        // User 실제 객체
        User mockUser = new User("test@example.com", "password123", "tester", null, null);

        when(mockProposal.getProposalId()).thenReturn(id);
        when(mockProposal.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(mockProposal.getUpdatedAt()).thenReturn(LocalDateTime.now());
        when(mockProposal.getProject()).thenReturn(mockProject);
        when(mockProposal.getSender()).thenReturn(mockUser);
        when(mockProposal.getDescription()).thenReturn(description);
        when(mockProposal.getProposedAmount()).thenReturn(amount);
        when(mockProposal.getPortfolioFiles()).thenReturn(List.of());
        when(mockProposal.getProposalStatus()).thenReturn(ProposalStatus.SUBMITTED);

        return mockProposal;
    }

    @Test
    @DisplayName("제안서 다건 조회")
    void getProposals() throws Exception {
        Proposal proposal = createDummyProposal(1L, "다건 조회 테스트", 1000);

        when(proposalService.getList()).thenReturn(List.of(proposal));

        mockMvc.perform(get("/api/v1/proposals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].proposalId").value(1))
                .andExpect(jsonPath("$.data[0].description").value("다건 조회 테스트"))
                .andExpect(jsonPath("$.data[0].proposedAmount").value(1000));

        verify(proposalService).getList();
    }

    @Test
    @DisplayName("제안서 단건 조회")
    void getProposal() throws Exception {
        Proposal proposal = createDummyProposal(1L, "단건 조회 테스트", 2000);

        when(proposalService.findById(1L)).thenReturn(proposal);

        mockMvc.perform(get("/api/v1/proposals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.proposalId").value(1))
                .andExpect(jsonPath("$.data.description").value("단건 조회 테스트"))
                .andExpect(jsonPath("$.data.proposedAmount").value(2000));

        verify(proposalService).findById(1L);
    }

    @Test
    @DisplayName("제안서 작성")
    void createProposal() throws Exception {
        ProposalRequestDto requestDto = new ProposalRequestDto(
                100L,
                "작성 테스트",
                3000,
                List.of()
        );

        Proposal proposal = createDummyProposal(2L, "작성 테스트", 3000);

        when(proposalService.create(eq(100L), eq("작성 테스트"), eq(3000), any(), eq(ProposalStatus.SUBMITTED)))
                .thenReturn(proposal);

        mockMvc.perform(post("/api/v1/proposals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.proposalId").value(2))
                .andExpect(jsonPath("$.data.description").value("작성 테스트"))
                .andExpect(jsonPath("$.data.proposedAmount").value(3000));

        verify(proposalService).create(eq(100L), eq("작성 테스트"), eq(3000), any(), eq(ProposalStatus.SUBMITTED));
    }

    @Test
    @DisplayName("제안서 삭제")
    void deleteProposal() throws Exception {
        // ✅ 실제 더미 ProposalResponseDto 생성
        ProposalResponseDto dto = new ProposalResponseDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                100L,
                new User(
                        "test@example.com",
                        "password123",
                        "tester",
                        null,
                        null
                ),
                "삭제 테스트 설명",
                9999,
                List.of(),
                ProposalStatus.SUBMITTED
        );

        // ✅ 서비스가 dto를 반환하도록 Stubbing
        when(proposalService.delete(1L)).thenReturn(dto);

        // ✅ DELETE 요청 수행
        mockMvc.perform(delete("/api/v1/proposals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.proposalId").value(1))
                .andExpect(jsonPath("$.data.description").value("삭제 테스트 설명"))
                .andExpect(jsonPath("$.data.proposedAmount").value(9999));

        verify(proposalService).delete(1L);
    }


    @Test
    @DisplayName("제안서 수정")
    void updateProposal() throws Exception {
        ProposalRequestBody requestBody = new ProposalRequestBody("수정된 설명", 5000, List.of());

        mockMvc.perform(put("/api/v1/proposals/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("4번 제안서가 수정되었습니다."));

        verify(proposalService).update(eq(4L), eq("수정된 설명"), eq(5000), any());
    }

    @Test
    @DisplayName("제안서 상태 변경")
    void updateStatus() throws Exception {
        ProposalStatusRequestBody requestBody = new ProposalStatusRequestBody(ProposalStatus.ACCEPTED);

        mockMvc.perform(put("/api/v1/proposals/5/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("5 번 제안서가 ACCEPTED 되었습니다."));

        verify(proposalService).update(eq(5L), eq(ProposalStatus.ACCEPTED));
    }
}
