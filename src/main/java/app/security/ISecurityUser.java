package app.security;

public interface ISecurityUser {
boolean verifyPassword(String pw);
void addRole(Role role);
void removeRole(Role role);
}
