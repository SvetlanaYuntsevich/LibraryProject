package by.javatr.dao.impl;

import by.javatr.dao.UserDAO;
import by.javatr.exception.DAOException;
import by.javatr.entity.Role;
import by.javatr.entity.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    private static final String fileName = "E:\\LibraryYuntsevich\\src\\by\\javatr\\txtFiles\\users.txt";
    private static final UserDAOImpl INSTANCE = new UserDAOImpl();
    private List<User> userList;

    private UserDAOImpl() {
        try {
            userList = readAllUsers();
        } catch (DAOException e) {
            userList = null;
        }
    }

    public static UserDAOImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean signIn(String login, String password) {
        for (User user : userList) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password))
                return true;
        }
        return false;
    }

    @Override
    public void register(User user) throws DAOException {
        for (User u : userList) {
            if (user.getLogin().equals(u.getLogin())) throw new DAOException("This login has already been taken");
        }
        user.setId(generateID());
        userList.add(user);
        updateUsersFile(userList);
    }

    @Override
    public boolean deleteAllUsers() throws DAOException {
        userList = new ArrayList<>();
        updateUsersFile(userList);
        return true;
    }

    @Override
    public boolean deleteUserByLogin(String login) throws DAOException {
        User user;
        Iterator<User> it = userList.iterator();
        while (it.hasNext()) {
            user = it.next();
            if (user.getLogin().equals(login)) {
                it.remove();
                updateUsersFile(userList);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        return userList;
    }

    @Override
    public String getUserByLogin(String userLogin) throws DAOException {
        String s = "";
        userList = readAllUsers();
        for (User user : userList) {
            if (user.getLogin().equals(userLogin)) {
                return user.toString();
            }
        }
        if (s.isEmpty()) throw new DAOException("There is no such User");
        return s;
    }

    //вспомогательные методы
    private int generateID() {
        int id = 1;
        for (User user : userList) {
            if (user.getId() >= id)
                id = user.getId() + 1;
        }
        return id;
    }

    private void updateUsersFile(List<User> userList) throws DAOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (User user : userList) {
                bw.write(user.toString());
                bw.write("\n");
            }
        } catch (IOException e) {
            throw new DAOException("Please check the file path");
        }
    }

    private List<User> readAllUsers() throws DAOException {
        List<User> userList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String str;
            while ((str = reader.readLine()) != null) {
                User user = initializeUser(str);
                userList.add(user);
            }
        } catch (IOException e) {
            throw new DAOException("File with users does not exist");
        }
        return userList;

    }

    private User initializeUser(String str) {
        User user = new User();
        if (str != null) {
            String[] parts = str.split(" ");
            // if (parts.length == 6) {
            for (String part : parts) {
                if (part != null) {
                    if (part.contains("id=")) {
                        String id = part.substring(part.indexOf("=") + 1, part.length() - 1);
                        user.setId(Integer.parseInt(id));
                    }
                    if (part.contains("firstName=")) {
                        String name = part.substring(part.indexOf("=") + 1, part.length() - 1);
                        user.setFirstName(name);
                    }
                    if (part.contains("surname=")) {
                        String surname = part.substring(part.indexOf("=") + 1, part.length() - 1);
                        user.setSurname(surname);
                    }
                    if (part.contains("login=")) {
                        String login = part.substring(part.indexOf("=") + 1, part.length() - 1);
                        user.setLogin(login);
                    }
                    if (part.contains("password=")) {
                        String password = part.substring(part.indexOf("=") + 1, part.length() - 1);
                        user.setPassword(password);
                    }
                    if (part.contains("role=")) {
                        String role = part.substring(part.indexOf("=") + 1, part.length() - 1);
                        if (role.equals(Role.READER.toString()))
                            user.setRole(Role.READER);
                        else user.setRole(Role.ADMIN);
                    }
                }
            }
        }
        //   }
        return user;
    }
}
