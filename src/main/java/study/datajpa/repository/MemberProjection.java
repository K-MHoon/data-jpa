package study.datajpa.repository;

/**
 * 네이티브 쿼리 + Projection 실습 전용 DTO
 */
public interface MemberProjection {

    Long getId();
    String getUsername();
    String getTeamName();
}
