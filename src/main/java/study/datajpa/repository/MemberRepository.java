package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, Long age);

    /**
     * 엔티티에 정의된 NamedQuery를 직접 가져온다.
     * @param username 이름
     * @return List Member
     */
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    /**
     * 직접 JPQL 구문을 입력해서 데이터를 가져온다.
     * @param username 이름
     * @param age 나이
     * @return List Member
     */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") Long age);

    /**
     * 유저의 이름 정보만 가져온다.
     * @return List 이름
     */
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();


    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(Long age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") Long age);

    /**
     * JPQL 직접 작성하는 페치 조인
     * @return
     */
    @Query("select m from Member m left join fetch m.team t")
    List<Member> findMemberFetchJoin();

    /**
     * 엔티티 그래프를 활용한 페치 조인
     * @return
     */
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    /**
     * JPQL, Fetch join 동시에!
     * @return
     */
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    /**
     * 메서드 이름과 패치 조인 동시에!
     * @param username
     * @return
     */
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);

    /**
     * NamedEntity Graph 활용
     * @param username
     * @return
     */
    @EntityGraph("Member.all")
    List<Member> findNamedEntityGraphByUsername(String username);

    /**
     * 쿼리 힌트 사용
     * @param username
     * @return
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    /**
     * Lock
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    /**
     * 네이티브 쿼리 사용
     * @param username
     * @return
     */
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    /**
     * Projection을 활용한 네이티브 쿼리 사용
     * @param pageable
     * @return
     */
    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            "from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true
    )
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
