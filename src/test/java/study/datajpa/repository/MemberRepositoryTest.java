package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        Long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        Long deletedCount = memberRepository.count();

        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10L);
        Member m2 = new Member("AAA", 20L);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15L);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20L);
        assertThat(result.size()).isEqualTo(1);
    }


    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10L);
        Member m2 = new Member("BBB", 20L);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> memberList = memberRepository.findByUsername("AAA");
        Member member = memberList.get(0);
        assertThat(member).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10L);
        Member m2 = new Member("BBB", 20L);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> memberList = memberRepository.findUser("AAA", 10L);
        Member member = memberList.get(0);
        assertThat(member).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10L);
        Member m2 = new Member("BBB", 20L);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // 단순 이름 조회
        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10L);
        Member m2 = new Member("BBB", 20L);
        memberRepository.save(m1);
        memberRepository.save(m2);


        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10L);
        m1.changeTeam(team);
        memberRepository.save(m1);

        // 단순 이름 조회
        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }


    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10L);
        Member m2 = new Member("AAA", 20L);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Member findMember = memberRepository.findMemberByUsername("AAA");
        System.out.println("findMember = " + findMember);

        Optional<Member> aaa = memberRepository.findOptionalByUsername("AAA");
        System.out.println("aaa.get() = " + aaa.get());

        List<Member> result = memberRepository.findListByUsername("afefeaw");
        System.out.println("result.size() = " + result.size());
    }

    @Test
    public void paging() {
        memberRepository.save(new Member("member1", 10L));
        memberRepository.save(new Member("member2", 10L));
        memberRepository.save(new Member("member3", 10L));
        memberRepository.save(new Member("member4", 10L));
        memberRepository.save(new Member("member5", 10L));
        memberRepository.save(new Member("member6", 10L));

        long age = 10L;
        int offset = 0;
        int limit = 3;

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> members = memberRepository.findByAge(age, pageRequest);
//        Slice<Member> members = memberRepository.findByAge(age, pageRequest);

        // then
        List<Member> content = members.getContent();
        long totalElements = members.getTotalElements();

        Page<MemberDto> toMap = members.map(m -> new MemberDto(m.getId(), m.getUsername(), null));


        // Page
        assertThat(content.size()).isEqualTo(3);
        assertThat(members.getTotalElements()).isEqualTo(6);
        assertThat(members.getNumber()).isEqualTo(0);
        assertThat(members.getTotalPages()).isEqualTo(2);
        assertThat(members.isFirst()).isTrue();
        assertThat(members.hasNext()).isTrue();

        // Slice
//        assertThat(content.size()).isEqualTo(3);
//        assertThat(members.getNumber()).isEqualTo(0);
//        assertThat(members.isFirst()).isTrue();
//        assertThat(members.hasNext()).isTrue();
    }


    /**
     * 벌크 업데이트 테스트 by JpaRepository;
     */
    @Test
    public void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10L));
        memberRepository.save(new Member("member2", 19L));
        memberRepository.save(new Member("member3", 21L));
        memberRepository.save(new Member("member4", 20L));
        memberRepository.save(new Member("member5", 40L));
        memberRepository.save(new Member("member6", 30L));

        // when
        int resultCount = memberRepository.bulkAgePlus(20L);
//        em.flush();
//        em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5.getAge() = " + member5.getAge());


        // then
        assertThat(resultCount).isEqualTo(4);

    }

    @Test
    public void findMemberLazy() {
        //given

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10L, teamA);
        Member member2 = new Member("member2", 10L, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        // select Member
        List<Member> members = memberRepository.findAll();

        //then
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            // 그냥 가져오면 프록시 객체 (가짜 객체)
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            // Team을 N번 만큼 가져온다. (실제로 DB에 요청을 날려서 초기화하는 과정)
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        //Fetch 조인
        List<Member> memberFetchJoin = memberRepository.findMemberFetchJoin();

        for (Member member : memberFetchJoin) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        //엔티티 그래프
        List<Member> memberEntityGraph = memberRepository.findAll();

        for (Member member : memberEntityGraph) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        em.flush();
        em.clear();

        Member member3 = new Member("member1", 24L, teamB);
        memberRepository.save(member3);

        em.flush();
        em.clear();

        List<Member> member1List = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : member1List) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        List<Member> namedEntityGraphByUsername = memberRepository.findNamedEntityGraphByUsername("member1");

        for (Member member : namedEntityGraphByUsername) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        //given
        Member member1 = new Member("member1", 10L);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");

        // 스냅샷이 없어서 해당 변경은 무시한다.
        findMember.changeUsername("member2");

        em.flush();
    }

    @Test
    public void lock() {
        //given
        Member member1 = new Member("member1", 10L);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");

    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void JpaEventBaseEntity() throws Exception {
        //g
        Member member = new Member("member1");
        memberRepository.save(member); // @PrePersist 발생

        Thread.sleep(100L);
        member.changeUsername("member2");

        em.flush(); // @PreUpdate
        em.clear();
        //w
        Member findMember = memberRepository.findById(member.getId()).get();

        //t
        System.out.println("findMember.create = " + findMember.getCreatedDate());
        System.out.println("findMember.update = " + findMember.getLastModifiedDate());
        System.out.println("findMember.getCreatedBy() = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());
    }

    @Test
    public void specBasic() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0L, teamA);
        Member m2 = new Member("m2", 0L, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0L, teamA);
        Member m2 = new Member("m2", 0L, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        //Probe
        Member member = new Member("m1");
        Team team = new Team("teamA");
        // 연결시키면, 연관관계까지 고려해서 조회한다.
        member.changeTeam(team);

        // 해당 속성이 있으면, 무시한다. (prmitive 타입은 값이 0이라도 있기 때문에 조회 조건이 나간다. 따라서 무시해야한다.)
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        //then
        assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void projections() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0L, teamA);
        Member m2 = new Member("m2", 0L, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections nestedClosedProjections : result) {
            String username = nestedClosedProjections.getUsername();
            String teamName = nestedClosedProjections.getTeam().getName();

            System.out.println("teamName = " + teamName);
            System.out.println("username = " + username);

        }

    }

    @Test
    public void nativeQuery() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0L, teamA);
        Member m2 = new Member("m2", 0L, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

//        Member result = memberRepository.findByNativeQuery("m1");
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection.getUsername() = " + memberProjection.getUsername());
            System.out.println("memberProjection.getTeamName() = " + memberProjection.getTeamName());
        }
    }

}