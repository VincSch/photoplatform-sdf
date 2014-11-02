
package de.htw.sdf.photoplatform.repository;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.htw.sdf.photoplatform.common.BaseTester;
import de.htw.sdf.photoplatform.common.Constants;
import de.htw.sdf.photoplatform.persistence.models.user.Role;
import de.htw.sdf.photoplatform.persistence.models.user.User;
import de.htw.sdf.photoplatform.persistence.models.user.UserBank;
import de.htw.sdf.photoplatform.persistence.models.user.UserProfile;
import de.htw.sdf.photoplatform.persistence.models.user.UserRole;

public class UserDAOTest extends BaseTester
{
    @Autowired
    protected UserDAO userDAO;

    @Autowired
    protected UserRoleDAO userRoleDAO;

    @Autowired
    protected RoleDAO roleDAO;

    @Autowired
    protected UserProfileDAO userProfileDAO;

    @Autowired
    protected UserBankDAO userBankDAO;

    @Before
    public void setUp() throws Exception
    {
        insertDestData();
    }

    @After
    public void tearDown() throws Exception
    {
        clearTables();
    }

    @Test
    public void createAndUpdateTest()
    {
        Role rolePhotographer = roleDAO.findOne(Constants.ROLE_PHOTOGRAPHER);
        String photographerTesterUsername = "PhotographerTester";
        String photographerTesterPassword = "photographerTestPass";
        String photographerTesterMail = "photographer@web.de";
        User photographer = createDefaultUser(
                photographerTesterUsername,
                photographerTesterPassword,
                photographerTesterMail,
                rolePhotographer,
                Boolean.FALSE,
                Boolean.FALSE);

        // Ein bisschen russisch stoert doch nicht :)
        String address = "мой адрес не дом и не улица мой адрес советский союз";
        String phone = "018765032";
        String company = "Photo AG";
        String homepage = "photo.de";
        UserProfile photographerProfile = new UserProfile();
        photographerProfile.setUser(photographer);
        photographerProfile.setAddress(address);
        photographerProfile.setPhone(phone);
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        photographerProfile.setBirthday(df.format(new Date()));
        photographerProfile.setCompany(company);
        photographerProfile.setHomepage(homepage);
        userProfileDAO.create(photographerProfile);

        String bic = "ABCDEF";
        String iban = "DE49178659403245";
        UserBank photographerBank = new UserBank();
        photographerBank.setUser(photographer);
        photographerBank.setReceiver(photographerTesterUsername);
        photographerBank.setBic(bic);
        photographerBank.setIban(iban);
        userBankDAO.create(photographerBank);

        // now test
        List<User> photographerList = userDAO.findByRoleId(Constants.ROLE_PHOTOGRAPHER);
        Assert.assertTrue("Genau zwei photographers", photographerList.size() == 2);
        User createdPhotographer = photographerList.get(1);
        Assert.assertNotNull(createdPhotographer.getId());
        Assert.assertTrue(createdPhotographer.getUsername().equals(photographerTesterUsername));
        Assert.assertTrue(createdPhotographer.getPassword().equals(photographerTesterPassword));
        Assert.assertTrue(createdPhotographer.getEmail().equals(photographerTesterMail));
        Assert.assertFalse(createdPhotographer.isEnabled());
        Assert.assertFalse(createdPhotographer.isAccountNonLocked());
        Assert.assertTrue(createdPhotographer.getEmail().equals(photographerTesterMail));
        Assert.assertTrue(createdPhotographer.getUserRoles().size() == 1);
        Assert.assertTrue(createdPhotographer
                .getUserRoles()
                .get(0)
                .getRole()
                .getId()
                .equals(Constants.ROLE_PHOTOGRAPHER));

        UserProfile createdPhotographerProfile = createdPhotographer.getUserProfile();
        Assert.assertNotNull(createdPhotographerProfile.getId());
        Assert.assertTrue(createdPhotographerProfile.getAddress().equals(address));
        Assert.assertTrue(createdPhotographerProfile.getPhone().equals(phone));
        Assert.assertTrue(createdPhotographerProfile.getBirthday().equals(df.format(new Date())));
        Assert.assertTrue(createdPhotographerProfile.getCompany().equals(company));
        Assert.assertTrue(createdPhotographerProfile.getHomepage().equals(homepage));

        UserBank createdPhotographerBank = createdPhotographer.getUserBank();
        Assert.assertNotNull(createdPhotographerBank.getId());
        Assert.assertTrue(createdPhotographerBank.getReceiver().equals(photographerTesterUsername));
        Assert.assertTrue(createdPhotographerBank.getBic().equals(bic));
        Assert.assertTrue(createdPhotographerBank.getIban().equals(iban));

        // now update test
        photographer.setEnabled(Boolean.TRUE);
        photographer.setAccountNonLocked(Boolean.TRUE);
        userDAO.update(photographer);

        User updatedPhotographer = userDAO.findOne(photographer.getId());
        Assert.assertTrue(updatedPhotographer.isEnabled());
        Assert.assertTrue(updatedPhotographer.isAccountNonLocked());

        String newPhone = "987653234";
        createdPhotographer.getUserProfile().setPhone(newPhone);
        userProfileDAO.update(createdPhotographer.getUserProfile());
        UserProfile updatedUserProfile = userProfileDAO.findOne(createdPhotographer
                .getUserProfile()
                .getId());
        Assert.assertTrue(updatedUserProfile.getPhone().equals(newPhone));

        String newBic = "RUSBANK";
        createdPhotographer.getUserBank().setBic(newBic);
        userBankDAO.update(createdPhotographer.getUserBank());
        UserBank updatedUserBank = userBankDAO.findOne(createdPhotographer.getUserBank().getId());
        Assert.assertTrue(updatedUserBank.getBic().equals(newBic));
    }

    @Test
    public void findTest()
    {
        // Init test Data
        Role admin = roleDAO.findOne(Constants.ROLE_ADMIN);
        String adminTesterUsername = "AdminTester";
        String adminTesterPassword = "testPass";
        String adminTesterMail = "admintester@web.de";
        createDefaultUser(
                adminTesterUsername,
                adminTesterPassword,
                adminTesterMail,
                admin,
                Boolean.TRUE,
                Boolean.TRUE);

        // Do test findByRole
        List<User> adminUsers = userDAO.findByRole(admin);
        Assert.assertTrue(
                "Ein Admin exestierte bereits und noch einer haben wir hinzugefügt.",
                adminUsers.size() == 2);
        Assert.assertTrue(adminUsers.get(1).getUsername().equals(adminTesterUsername));
        Assert.assertTrue(adminUsers.get(1).getPassword().equals(adminTesterPassword));
        Assert.assertTrue(adminUsers.get(1).getEmail().equals(adminTesterMail));
        Assert.assertTrue(
                "Admin hat genau eine Rolle!",
                adminUsers.get(1).getUserRoles().size() == 1);
        Assert.assertTrue(adminUsers
                .get(1)
                .getUserRoles()
                .get(0)
                .getRole()
                .getId()
                .equals(Constants.ROLE_ADMIN));

        // Do test findAllNotAdminUsers
        Role rolePhotographer = roleDAO.findOne(Constants.ROLE_PHOTOGRAPHER);
        String photographerTesterUsername = "PhotographerTester";
        String photographerTesterPassword = "photographerTestPass";
        String photographerTesterMail = "photographer@web.de";
        createDefaultUser(
                photographerTesterUsername,
                photographerTesterPassword,
                photographerTesterMail,
                rolePhotographer,
                Boolean.FALSE,
                Boolean.FALSE);
        List<User> notAdminUsers = userDAO.findAllNotAdminUsers();
        Assert.assertTrue(
                "Drei Users: Peter-Customer; Sergej-Photographer: PhotographerTester-Photographer",
                notAdminUsers.size() == 3);
        Set<Long> existedRoles = new HashSet<Long>();
        for (User notAdminUser : notAdminUsers)
        {
            for (UserRole notAdminRole : notAdminUser.getUserRoles())
            {
                existedRoles.add(notAdminRole.getRole().getId());
            }
        }
        Assert.assertFalse(
                "Fehler, wenn role admin vorhanden ist.",
                existedRoles.contains(Constants.ROLE_ADMIN));

        // Do test findByEnabled
        List<User> enabledUsers = userDAO.findByEnabled(Boolean.TRUE);
        Assert
                .assertTrue(
                        "Der PhotographerTester darf nicht dabei sein.",
                        enabledUsers.size() == 4);
        for (User enabledUser : enabledUsers)
        {
            if (enabledUser.getUsername().equals(photographerTesterUsername))
            {
                Assert.fail("Der PhotographerTester darf nicht dabei sein.");
            }
        }

        List<User> notEnabledUsers = userDAO.findByEnabled(Boolean.FALSE);
        Assert.assertTrue(
                "Nur Der PhotographerTester darf dabei sein.",
                notEnabledUsers.size() == 1);
        Assert.assertTrue(notEnabledUsers.get(0).getUsername().equals(photographerTesterUsername));

        // Do test findByAccountLocked
        List<User> lockedUsers = userDAO.findByAccountLocked(Boolean.TRUE);
        Assert.assertTrue("Der PhotographerTester darf nicht dabei sein.", lockedUsers.size() == 4);
        for (User lockedUser : lockedUsers)
        {
            if (lockedUser.getUsername().equals(photographerTesterUsername))
            {
                Assert.fail("Der PhotographerTester darf nicht dabei sein.");
            }
        }

        List<User> notLockedUsers = userDAO.findByAccountLocked(Boolean.FALSE);
        Assert
                .assertTrue(
                        "Nur Der PhotographerTester darf dabei sein.",
                        notLockedUsers.size() == 1);
        Assert.assertTrue(notLockedUsers.get(0).getUsername().equals(photographerTesterUsername));
    }

    private User createDefaultUser(
            String username,
            String password,
            String email,
            Role role,
            Boolean enabled,
            Boolean locked)
    {
        User defaultUser = new User();
        defaultUser.setUserName(username);
        defaultUser.setPassword(password);
        defaultUser.setEnabled(enabled);
        defaultUser.setAccountNonLocked(locked);
        defaultUser.setEmail(email);
        userDAO.create(defaultUser);

        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setUser(defaultUser);
        userRoleDAO.create(userRole);

        return defaultUser;
    }
}