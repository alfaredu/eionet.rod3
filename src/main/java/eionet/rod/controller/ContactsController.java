package eionet.rod.controller;

import eionet.directory.DirServiceException;
import eionet.directory.DirectoryService;
import eionet.directory.dto.RoleDTO;
import eionet.rod.util.BreadCrumbs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/contacts")
public class ContactsController {

    private static final Log logger = LogFactory.getLog(ContactsController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String contacts(String roleId, RedirectAttributes redirectAttributes, Model model, HttpServletRequest request) {

        boolean isDirectoryError = false;

        if (roleId != null) {
            try {
                int index = roleId.lastIndexOf("-");
                String parentRoleId = null;
                if (index != -1) {
                    parentRoleId = roleId.substring(0, index);
                }
                RoleDTO dirRole = DirectoryService.getRoleDTO(roleId);
                model.addAttribute("parentRoleId", parentRoleId);
                model.addAttribute("dirRole", dirRole);
            } catch (DirServiceException e) {
                isDirectoryError = true;
                logger.info("Directory exception " + e.getMessage(), e);
            }
        }

        model.addAttribute("isDirectoryError", isDirectoryError);
        model.addAttribute("title", "Contacts");
        BreadCrumbs.set(model, "Contacts");

        String url = request.getHeader("Referer");

        if (url != null) {
            url = url.substring(url.lastIndexOf("/") + 1);
            if ("deadlines".equals(url)) {
                model.addAttribute("activeTab", "spatial");
            } else {
                model.addAttribute("activeTab", "obligations");
            }
        } else {
            model.addAttribute("activeTab", "spatial");
        }

        return "contacts";
    }

}
