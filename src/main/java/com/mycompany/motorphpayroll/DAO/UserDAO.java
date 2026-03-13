package com.mycompany.motorphpayroll.DAO;

import com.mycompany.motorphpayroll.model.User;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    public Optional<User> findById(String username) {
    User user = CSVReaderUtil.getUserByUsername(username);
    return Optional.ofNullable(user);
}

    public List<User> findAll() {
        try {
            return new ArrayList<>(CSVReaderUtil.readUsersFromLoginCSV().values());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void save(User user) throws Exception {
        CSVReaderUtil.addUserToLoginCSV(user);
    }

    public boolean update(User user) throws Exception {
        return CSVReaderUtil.updateUserInLoginCSV(user);
    }

    public boolean delete(String username) throws Exception {
        List<User> users = findAll();
        boolean removed = users.removeIf(u -> u.getUsername().equals(username));
        if (removed) {
            CSVReaderUtil.writeAllUsersToLoginCSV(users, CSVReaderUtil.getWritableLoginCredentialsCsvPath());
        }
        return removed;
    }
}