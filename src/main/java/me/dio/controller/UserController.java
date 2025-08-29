package me.dio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.dio.controller.dto.UserDto;
import me.dio.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/users")
@Tag(name = "Controlador de Usuários", description = "API RESTful para gerenciamento de usuários.")
public record UserController(UserService userService) {

    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Recupera uma lista de todos os usuários cadastrados")
    @ApiResponses(value = { 
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso")
    })
    public ResponseEntity<List<UserDto>> findAll() {
        var users = userService.findAll();
        var usersDto = users.stream().map(UserDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(usersDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter usuário por ID", description = "Recupera um usuário específico com base no seu ID")
    @ApiResponses(value = { 
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        var user = userService.findById(id);
        return ResponseEntity.ok(new UserDto(user));
    }

    @GetMapping("/ativos")
    @Operation(summary = "Obter usuários ativos", description = "Retorna apenas os usuários que estão ativos")
    @ApiResponses(value = { 
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso")
    })
    public ResponseEntity<List<UserDto>> findUsuariosAtivos() {
        var usuariosAtivos = userService.findAll().stream()
                                        .filter(user -> Boolean.TRUE.equals(user.getActive()))
                                        .map(UserDto::new)
                                        .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosAtivos);
    }

    @PostMapping
    @Operation(summary = "Criar um novo usuário", description = "Cria um novo usuário e retorna os dados do usuário criado")
    @ApiResponses(value = { 
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        var user = userService.create(userDto.toModel());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();
        return ResponseEntity.created(location).body(new UserDto(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente com base no seu ID")
    @ApiResponses(value = { 
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserDto userDto) {
        var user = userService.update(id, userDto.toModel());
        return ResponseEntity.ok(new UserDto(user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar usuário", description = "Deleta um usuário existente com base no seu ID")
    @ApiResponses(value = { 
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
