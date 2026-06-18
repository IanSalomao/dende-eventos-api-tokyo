package br.com.softhouse.dende.model.dto;

// [AVALIAÇÃO - Item 4] O DTO possui o campo 'email', mas o email já é recebido como @PathVariable no endpoint.
// Isso cria redundância: o cliente precisa enviar o email duas vezes (na URL e no corpo da requisição).
// Nos controllers, o email do body (body.senha()) é ignorado e o do path é usado.
// Sugestão: remova o campo 'email' do DTO, deixando apenas 'senha'.
// Código sugerido: public record ReativarUsuarioDTO(String senha) {}
public record ReativarUsuarioDTO(
        String email,
        String senha
)
{}