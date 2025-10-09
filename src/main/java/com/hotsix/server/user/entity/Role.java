package com.hotsix.server.user.entity;

    public enum Role {
        CLIENT, FREELANCER;

        @Override
        public String toString() {
            return name(); // "CLIENT", "FREE"
        }
    }

