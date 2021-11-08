UPDATE user
    SET username = (SELECT SUBSTRING_INDEX(email, '@', 1))
    WHERE username = '';