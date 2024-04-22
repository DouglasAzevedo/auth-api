package br.com.stic.templateauth.domain.user;

public record RegisterDTO(String login, String password, UserRole role) {
}
