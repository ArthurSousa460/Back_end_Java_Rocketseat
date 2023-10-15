package br.com.arthur.local.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //Define a classe como controller
@RequestMapping("/users") // definindo a rota do controller
public class UserController {

    @Autowired
    private IUserRepository userRepository; // Classe que tem os métodos de comunicação com DB

    // ResponseEntity - Classe responsavel pela respostas da entidade(classe que possui a conexão com banco
    // @Requestbody - Anotação que define o que vai ser enviado no body da API
    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel){
       var user = this.userRepository.findByuserName(userModel.getUserName()); //buscando o username no banco de dados

        // verificando se o usuário existe
        if(user != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O usuário já existe");
        }

        var passwordHashed = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());

        userModel.setPassword(passwordHashed);



        // cadastrando o usuário no banco
       var userCreated =  this.userRepository.save(userModel);
       return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }
}
