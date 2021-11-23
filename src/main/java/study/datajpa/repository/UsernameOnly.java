package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    // username과 age를 더해서 넣어준다. [SPEL] >> Open Projection
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
