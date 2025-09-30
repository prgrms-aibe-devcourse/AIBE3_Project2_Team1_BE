package com.hotsix.server.proposal.entity;

import com.hotsix.server.global.entity.BaseEntity;
import com.hotsix.server.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;
import com.hotsix.server.user.entity.User;
import org.hibernate.service.spi.ServiceException;

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
    @JoinColumn(name = "freelancer_user_id", nullable = false)
    private User freelancer;

    @Lob //JPA에서 긴 텍스트(CLOB)나 바이너리(BLOB) 데이터를 DB에 저장할 때 쓰는 어노테이션
    private String description;

    private Integer proposedAmount;

    @Enumerated(EnumType.STRING)
    private ProposalStatus proposalStatus; /**
     * Create a Proposal with the specified project, freelancer, description, proposed amount, and status.
     *
     * @param project the project this proposal is for
     * @param freelancer the user who submitted the proposal
     * @param description detailed proposal text
     * @param proposedAmount the proposed monetary amount
     * @param proposalStatus the initial status of the proposal (e.g., DRAFT, SUBMITTED, ACCEPTED, REJECTED)
     */

    public Proposal(Project project, User freelancer, String description, Integer proposedAmount, ProposalStatus proposalStatus) {
        this.project = project;
        this.freelancer = freelancer;
        this.description = description;
        this.proposedAmount = proposedAmount;
        this.proposalStatus = proposalStatus;
    }

    /**
     * Ensure the given freelancer is authorized to delete this proposal.
     *
     * @param freelancer the user attempting deletion; compared by name to the proposal's freelancer
     * @throws ServiceException if the provided freelancer's name does not match this proposal's freelancer name
     */
    public void checkCanDelete(User freelancer) {
        if(!freelancer.getName().equals(this.freelancer.getName())){
            throw new ServiceException("%d번 글 삭제 권한이 없습니다.".formatted(getProposalId()));
        }
    }

    /**
     * Verifies that the given freelancer is the owner of this proposal.
     *
     * @param freelancer the freelancer to validate against the proposal's owner
     * @throws ServiceException if the provided freelancer's name does not match the proposal owner's name
     */
    public void checkCanModify(User freelancer) {
        if(!freelancer.getName().equals(this.freelancer.getName())){
            throw new ServiceException("%d번 글 수정 권한이 없습니다.".formatted(getProposalId()));
        }
    }

    /**
     * Update the proposal's description and proposed amount.
     *
     * @param description   the new detailed proposal text
     * @param proposedAmount the new proposed monetary amount
     */
    public void modify(String description, Integer proposedAmount) {
        this.description = description;
        this.proposedAmount = proposedAmount;
    }
}
