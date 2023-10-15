package br.com.arthur.local.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.arthur.local.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;


//classe responsável pela autenticação
@Component
public class FilterTaskAuth  extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        var servletPath = request.getServletPath();
        // autenticação só será feita no /tasks
        if(servletPath.startsWith("/tasks/")){
            // pegar a autenticação(usuário e senha)
            var authorization = request.getHeader("Authorization");
            var authEncoded = authorization.substring("Basic".length()).trim();

            byte[] authDecode = Base64.getDecoder().decode(authEncoded);
            var authString = new String(authDecode);

            String[] credentials = authString.split(":");

            String userName = credentials[0];
            String password = credentials[1];

            //validando usuário

            var user = this.userRepository.findByuserName(userName);
            if(user == null){
                response.sendError(401);
            }else{
                var passwordVerify =  BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if(passwordVerify.verified){
                    request.setAttribute("idUser", user.getId()); // envia o usuário para controller
                    filterChain.doFilter(request, response);
                }else{
                    response.sendError(401);
                }
            }
        }else{
            filterChain.doFilter(request, response);
        }



    }
}
