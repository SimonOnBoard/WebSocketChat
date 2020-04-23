package ru.itis.websockets.content;

public interface FormContentLoadable {
    String loginForm = "<form name='f' id=\"form\" action=\"/login\" method='POST'>\n" +
            "        <table>\n" +
            "            <tr>\n" +
            "                <td>User:</td>\n" +
            "                <td><input type='text' name='login'></td>\n" +
            "            </tr>\n" +
            "            <tr>\n" +
            "                <td>Password:</td>\n" +
            "                <td><input type='password' name='password' /></td>\n" +
            "            </tr>\n" +
            "            <tr>\n" +
            "                <td><input name=\"submit\" type=\"button\" value=\"Отправить\" onclick=\"x()\"/></td>\n" +
            "            </tr>\n" +
            "        </table>\n" +
            "    </form>";
    String registrationForm = "<div class=\"reg-container\" id=\"forms\">\n" +
            "            <form name=\"reg\" id=\"form\" action=\"/registration\" method=\"post\"><h1>\n" +
            "                    Форма регистрации пользователя</h1>\n" +
            "\n" +
            "                <div id=\"namer\">\n" +
            "                    <div id=\"namer-input\">\n" +
            "                        <label>Full nick</label>\n" +
            "                        <input type=\"text\" id=\"nick\" name=\"nick\" placeholder=\"Type your nick\" required>\n" +
            "                    </div>\n" +
            "                </div>\n" +
            "\n" +
            "                <div id=\"namer\">\n" +
            "                    <div id=\"namer-input\">\n" +
            "                        <label>password</label>\n" +
            "                        <input type=\"password\" id=\"password\" name=\"password\" placeholder=\"Type your password\" required>\n" +
            "                    </div>\n" +
            "                </div>\n" +
            "\n" +
            "                <div id=\"namer\">\n" +
            "                    <div id=\"namer-input\">\n" +
            "                        <label>Login</label>\n" +
            "                        <input type=\"text\" id=\"email\" name=\"email\" placeholder=\"Type your email\" required/>\n" +
            "                    </div>\n" +
            "                </div>\n"  +
            "                <button type=\"button\" class=\"btn btn-danger btn-lg\" onclick=\"x()\">Зарегистрироваться</button>\n" +
            "            </form>\n" +
            "        </div>";
}
