/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import com.court.model.User;
import com.court.model.UserHasUserRole;

/**
 *
 * @author Shanaka P
 */
public class LoggedSessionHandler {

    private UserHasUserRole urole;

    public LoggedSessionHandler(UserHasUserRole urole) {
        this.urole = urole;
    }

    public User loggedUser() {
        return this.urole.getUser();
    }

    public void updateLoggedUser(User u) {
        this.urole.setUser(u);
    }

    public boolean checkPrivilegeExist(int privId) {
        return this.urole.getUserRole().getPrivCats().stream()
                .anyMatch(p -> p.getUsrRolePrivilage().getPrivId() == privId);
    }

    public UserHasUserRole getUrole() {
        return urole;
    }

    public void setUrole(UserHasUserRole urole) {
        this.urole = urole;
    }
}
