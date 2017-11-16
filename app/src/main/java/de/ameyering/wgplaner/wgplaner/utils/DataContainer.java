package de.ameyering.wgplaner.wgplaner.utils;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.swagger.client.model.*;

public abstract class DataContainer {

    /**
     * DataContainer for ShoppingListItems
     */

    public static abstract class ShoppingListItems {
        private static ArrayList<ListItem> shoppingListItems = new ArrayList<>();

        public static ArrayList<ListItem> getShoppingListItems() {
            ArrayList<ListItem> items = new ArrayList<>();
            items.addAll(shoppingListItems);
            return items;
        }

        public static void addShoppingListItem(String name, int number, List<String> requestedFor,
            String requestedBy) {
            ListItem item = new ListItem();
            item.setTitle(name);
            item.setRequestedFor(requestedFor);
            item.setRequestedBy(requestedBy);

            int pos = contains(item);

            if (pos == -1) {
                shoppingListItems.add(item);

            } else {
                //TODO: Increment number
            }
        }

        private static int contains(ListItem item) {
            for (int i = 0; i < shoppingListItems.size(); i++) {
                ListItem listItem = shoppingListItems.get(i);

                if (
                    listItem.getTitle() != null && item.getTitle() != null &&
                    listItem.getRequestedFor() != null && item.getRequestedFor() != null) {
                    if (listItem.getTitle().equals(item.getTitle()) &&
                        listItem.getRequestedFor().equals(item.getRequestedFor())) {
                        return i;
                    }
                }
            }

            return -1;
        }

        public static void removeShoppingListItem(ListItem item) {
            int pos = contains(item);

            if (pos != -1) {
                shoppingListItems.remove(pos);
            }
        }
    }

    /**
     * DataContainer for User-Instances
     **/

    public static abstract class Users {
        private static User me = new User();
        private static ArrayList<User> users = new ArrayList<>();

        public static User getMe() {
            return me;
        }

        public static void setMe(User me) {
            Users.me = me;
        }

        protected static void addAllUsers(ArrayList<User> users) {
            if (users != null) {
                Users.users.addAll(users);

            }
        }

        @Nullable
        public static String getDisplayNameByUid(String uid) {
            for (User user : users) {
                if (user.getUid().equals(uid)) {
                    return user.getDisplayName();
                }
            }

            return null;
        }

        public static ArrayList<User> getAll() {
            return (ArrayList<User>) users.clone(); //returns cloned List to avoid changes on Users.users
        }

        @Nullable
        public static User getUser(int index) {
            if (index < users.size()) {
                return users.get(index);
            }

            return null;
        }

        protected static void removeUser(User user) {
            users.remove(user);
        }
    }

    /**
     * DataContainer for the group
     */

    public static abstract class Groups {
        private static Group group;
        private static List<User> members;

        public static void setMembers(List<User> members) {
            Groups.members = members;
        }

        public static Group getGroup() {
            return group;
        }

        public static void setGroup(Group group) {
            Groups.group = group;
        }

        public static List<User> getMembers() {
            if (members != null) {
                return members;
            }

            return new ArrayList<>();
        }
    }
}
