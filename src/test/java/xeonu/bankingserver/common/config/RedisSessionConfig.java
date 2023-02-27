package xeonu.bankingserver.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

@Configuration
@EnableRedisHttpSession
public class RedisSessionConfig extends AbstractHttpSessionApplicationInitializer {

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisServer redisServer = new RedisServerBuilder().bind("127.0.0.1").port(6379).build();
    RedisConnectionFactory connectionFactory = new LettuceConnectionFactory(
        new RedisStandaloneConfiguration("127.0.0.1", 6379));
    redisServer.start();
    return connectionFactory;
  }


  @Bean
  public RedisHttpSessionConfiguration redisHttpSessionConfiguration() {
    RedisHttpSessionConfiguration configuration = new RedisHttpSessionConfiguration();
    configuration.setMaxInactiveIntervalInSeconds(1800);
    return configuration;
  }
}
