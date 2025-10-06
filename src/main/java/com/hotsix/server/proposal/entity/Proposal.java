package com.hotsix.server.proposal.entity;

import com.hotsix.server.global.entity.BaseEntity;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.proposal.exception.ProposalErrorCase;
import com.hotsix.server.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "proposals") // 프리랜서가 클라이언트에게 프로젝트를 하고싶다고 보낸 제안서 관련 테이블
public class Proposal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proposalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id", referencedColumnName = "userId", nullable = false)
    private User sender;

    @Lob //JPA에서 긴 텍스트(CLOB)나 바이너리(BLOB) 데이터를 DB에 저장할 때 쓰는 어노테이션
    private String description;

    private Integer proposedAmount;

//    @Builder.Default
//    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ProposalFile> portfolioFiles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ProposalStatus proposalStatus; // DRAFT, SUBMITTED, ACCEPTED, REJECTED

    public void checkCanDelete(User sender) {
        if(!sender.getUserId().equals(this.sender.getUserId())){
            throw new ApplicationException(ProposalErrorCase.FORBIDDEN_DELETE);
        }
    }

    public void checkCanModify(User sender) {
        if(!sender.getUserId().equals(this.sender.getUserId())){
            throw new ApplicationException(ProposalErrorCase.FORBIDDEN_UPDATE);
        }
    }

    public void modify(String description, Integer proposedAmount/*, List<ProposalFile> proposalFiles*/) {
        this.description = description;
        this.proposedAmount = proposedAmount;
        //this.portfolioFiles = proposalFiles;
    }

    public void modify(ProposalStatus proposalStatus) {
        this.proposalStatus = proposalStatus;
    }
}
