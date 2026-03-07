package com.mycompany.motorphpayroll.DAO;

import com.mycompany.motorphpayroll.model.User;
import com.mycompany.motorphpayroll.util.CSVReaderUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements DAO<User, String> {

    @Override
    public Optional<User> findById(String username) {
        return CSVReaderUtil.getUserByUsername(username);
    }

    @Override
    public List<User> findAll() {
        try {
            return new ArrayList<>(CSVReaderUtil.readUsersFromLoginCSV().values());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void save(User user) throws Exception {
        CSVReaderUtil.addUserToLoginCSV(user);
    }

    @Override
    public boolean update(User user) throws Exception {
        return CSVReaderUtil.updateUserInLoginCSV(user);
    }

    @Override
    public boolean delete(String username) throws Exception {
        // Implementation depends on if you want to allow deleting users
        List<User> users = findAll();
        boolean removed = users.removeIf(u -> u.getUsername().equals(username));
        if (removed) {
            CSVReaderUtil.writeAllUsersToLoginCSV(users, CSVReaderUtil.getWritableLoginCredentialsCsvPath());
        }
        return removed;
    }
}
