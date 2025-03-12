package com.guudint.clicdo.security.controller;

import java.util.Arrays;
import java.util.List;

import com.vcc.camelone.cac.services.PermExMenu;

public class DummyMenu {

    public static List<PermExMenu> getExMenus() {
        PermExMenu menu1 = new PermExMenu();
        menu1.setName("Administrator");
        menu1.setIcon("grid_view");
        menu1.setPath("/");
        PermExMenu child1Menu1 = new PermExMenu();
        child1Menu1.setName("Manage Truck");
        child1Menu1.setPath("/administrations/truck-management/list");
        child1Menu1.setIconText("CC");
        PermExMenu child2Menu1 = new PermExMenu();
        child2Menu1.setName("Manage Driver");
        child2Menu1.setPath("/administrations/driver-management/list");
        child2Menu1.setIconText("CC");
        PermExMenu child3Menu1 = new PermExMenu();
        child3Menu1.setName("Manage Location");
        child3Menu1.setPath("/administrations/location-management/list");
        child3Menu1.setIconText("CC");
        PermExMenu child4Menu1 = new PermExMenu();
        child4Menu1.setName("Manage Rental");
        child4Menu1.setPath("/administrations/rental-management/list");
        child4Menu1.setIconText("CC");
        PermExMenu child5Menu1 = new PermExMenu();
        child5Menu1.setName("Manage Contract");
        child5Menu1.setPath("/administrations/contract-management/list");
        child5Menu1.setIconText("CC");
        menu1.setChildren(Arrays.asList(child1Menu1, child2Menu1, child3Menu1, child4Menu1, child5Menu1));

        PermExMenu menu2 = new PermExMenu();
        menu2.setName("Job");
        menu2.setIcon("grid_view");
        menu2.setPath("/");
        PermExMenu child1Menu2 = new PermExMenu();
        child1Menu2.setName("Job List");
        child1Menu2.setPath("/applications/services/job/list");
        child1Menu2.setIconText("CC");
        PermExMenu child2Menu2 = new PermExMenu();
        child2Menu2.setName("Job Create New");
        child2Menu2.setPath("/applications/services/job/job/new/-");
        child2Menu2.setIconText("CC");
        menu2.setChildren(Arrays.asList(child1Menu2, child2Menu2));

        PermExMenu menu3 = new PermExMenu();
        menu3.setName("Help");
        menu3.setIcon("help_outline");
        menu3.setPath("/help");
        return Arrays.asList(menu1, menu2, menu3);
    }
}
