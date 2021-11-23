package study.datajpa.repository;

public class UsernameOnlyDto {

    private final String username;

    // 파라미터 명을 분석해서 사용한다.
    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
