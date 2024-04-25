package org.book.commerce.userservice.domain;


public enum Role {
    USER("회원"),
    ADMIN("관리자");

    private final String rolename;

    Role(String rolename) {
        this.rolename = rolename;
    }

    public static Role valueOfTerm(String rolename) throws Exception {
        for(Role role:values()){
            if(rolename.equals(role.rolename)){
                return role;
            }
        }
        throw new Exception("존재하지 않는 권한입니다.");
    }
}
