package backend.mangoapp.configuration;

import backend.mangoapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Autowired
    private UserRepository userRepository;


}