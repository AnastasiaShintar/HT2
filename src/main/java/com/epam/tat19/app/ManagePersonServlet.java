package com.epam.tat19.app;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ManagePersonServlet extends HttpServlet {

    // Идентификатор для сериализации/десериализации.
    private static final long serialVersionUID = 1L;

    // Основной объект, хранящий данные телефонной книги.
    private Phonebook phonebook;

    public ManagePersonServlet() {
        // Вызов родительского конструктора.
        super();

        // Создание экземпляра телефонной книги.
        try {
            this.phonebook = Phonebook.getInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Валидация ФИО и генерация сообщения об ошибке в случае невалидных данных.
    private String validatePersonFMLName(Person person) {
        String error_message = "";

        if (!person.validateFMLNamePart(person.getName(), false)) {
            error_message += "Имя должно быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
        }

        if (!person.validateFMLNamePart(person.getSurname(), false)) {
            error_message += "Фамилия должна быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
        }

        if (!person.validateFMLNamePart(person.getMiddlename(), true)) {
            error_message += "Отчество должно быть строкой от 0 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
        }

        return error_message;
    }

    private String validatePhone(String phone) {
        Matcher matcher = Pattern.compile("[\\d+\\-#]{2,50}").matcher(phone);
        if (!matcher.matches()) {
            return "Требования к телефонному номеру: от 2 до 50 символов: цифра, +, -, #";
        } else {
            return "";
        }
    }

    // Реакция на GET-запросы.
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Обязательно ДО обращения к любому параметру нужно переключиться в UTF-8,
        // иначе русский язык при передаче GET/POST-параметрами превращается в "кракозябры".
        request.setCharacterEncoding("UTF-8");

        // В JSP нам понадобится сама телефонная книга. Можно создать её экземпляр там,
        // но с архитектурной точки зрения логичнее создать его в сервлете и передать в JSP.
        request.setAttribute("phonebook", this.phonebook);

        // Хранилище параметров для передачи в JSP.
        HashMap<String, String> jsp_parameters = new HashMap<String, String>();

        // Диспетчеры для передачи управления на разные JSP (разные представления (view)).
        RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePerson.jsp");
        RequestDispatcher dispatcher_for_phone_manager = request.getRequestDispatcher("/ManagePhone.jsp");
        RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/List.jsp");

        // Действие (action) и идентификатор записи (id) над которой выполняется это действие.
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        String phoneId = request.getParameter("phoneid");

        // Если идентификатор и действие не указаны, мы находимся в состоянии
        // "просто показать список и больше ничего не делать".
        if ((action == null) && (id == null) && (phoneId == null)) {
            request.setAttribute("jsp_parameters", jsp_parameters);
            dispatcher_for_list.forward(request, response);
        }
        // Если же действие указано, то...
        else {
            Person current_person;
            switch (action) {
                // Добавление записи.
                case "add":
                    // Создание новой пустой записи о пользователе.
                    Person empty_person = new Person();

                    // Подготовка параметров для JSP.
                    jsp_parameters.put("current_action", "add");
                    jsp_parameters.put("next_action", "add_go");
                    jsp_parameters.put("next_action_label", "Добавить");

                    // Установка параметров JSP.
                    request.setAttribute("person", empty_person);
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_manager.forward(request, response);
                    break;
                //Добавление номера
                case "addphone":
                    current_person = this.phonebook.getPerson(id);

                    // Подготовка параметров для JSP.
                    jsp_parameters.put("current_action", "add_phone");
                    jsp_parameters.put("next_action", "add_phone_go");
                    jsp_parameters.put("next_action_label", "Добавить номер");
                    jsp_parameters.put("long_name", current_person.getLongName());

                    request.setAttribute("phone_number", "");
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_phone_manager.forward(request, response);
                    break;

                // Редактирование записи.
                case "edit":
                    // Извлечение из телефонной книги информации о редактируемой записи.
                    Person editable_person = this.phonebook.getPerson(id);

                    // Подготовка параметров для JSP.
                    jsp_parameters.put("current_action", "edit");
                    jsp_parameters.put("next_action", "edit_go");
                    jsp_parameters.put("next_action_label", "Сохранить");

                    // Установка параметров JSP.
                    request.setAttribute("person", editable_person);
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_manager.forward(request, response);
                    break;
                case "editphone":
                    // Подготовка параметров для JSP.
                    String editable_phone = phonebook.getPhone(id, phoneId);
                    current_person = this.phonebook.getPerson(id);

                    jsp_parameters.put("current_action", "edit_phone");
                    jsp_parameters.put("next_action", "edit_phone_go");
                    jsp_parameters.put("next_action_label", "Сохранить номер");
                    jsp_parameters.put("long_name", current_person.getLongName());

                    request.setAttribute("phone_number", editable_phone);
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_phone_manager.forward(request, response);
                    break;

                // Удаление записи.
                case "delete":

                    // Если запись удалось удалить...
                    if (phonebook.deletePerson(id)) {
                        jsp_parameters.put("current_action_result", "DELETION_SUCCESS");
                        jsp_parameters.put("current_action_result_label", "Удаление выполнено успешно");
                    }
                    // Если запись не удалось удалить (например, такой записи нет)...
                    else {
                        jsp_parameters.put("current_action_result", "DELETION_FAILURE");
                        jsp_parameters.put("current_action_result_label", "Ошибка удаления (возможно, запись не найдена)");
                    }

                    // Установка параметров JSP.
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_list.forward(request, response);
                    break;
                // Удаление телефона.
                case "deletephone":
                    // Если запись удалось удалить...
                    if (phonebook.deletePhone(phoneId, id)) {
                        jsp_parameters.put("current_action_result", "DELETION_SUCCESS");
                        jsp_parameters.put("current_action_result_label", "Удаление выполнено успешно");
                    }
                    // Если запись не удалось удалить (например, такой записи нет)...
                    else {
                        jsp_parameters.put("current_action_result", "DELETION_FAILURE");
                        jsp_parameters.put("current_action_result_label", "Ошибка удаления (возможно, запись не найдена)");
                    }

                    // Установка параметров JSP.
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    request.getRequestDispatcher("/?action=edit&id=" + id).forward(request, response);
                    break;
            }
        }

    }

    // Реакция на POST-запросы.
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Обязательно ДО обращения к любому параметру нужно переключиться в UTF-8,
        // иначе русский язык при передаче GET/POST-параметрами превращается в "кракозябры".
        request.setCharacterEncoding("UTF-8");

        // В JSP нам понадобится сама телефонная книга. Можно создать её экземпляр там,
        // но с архитектурной точки зрения логичнее создать его в сервлете и передать в JSP.
        request.setAttribute("phonebook", this.phonebook);

        // Хранилище параметров для передачи в JSP.
        HashMap<String, String> jsp_parameters = new HashMap<String, String>();

        // Действие (add_go, edit_go, add_phone_go, edit_phone_go) и идентификатор записи (id) над которой выполняется это действие.
        String add_go = request.getParameter("add_go");
        String add_phone_go = request.getParameter("add_phone_go");
        String edit_go = request.getParameter("edit_go");
        String edit_phone_go = request.getParameter("edit_phone_go");
        String id = request.getParameter("id");
        String phoneid = request.getParameter("phoneid");

        // Диспетчеры для передачи управления на разные JSP (разные представления (view)).
        RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePerson.jsp");
        RequestDispatcher dispatcher_for_phone_manager = request.getRequestDispatcher("/ManagePhone.jsp");
        RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/List.jsp");

        // Добавление записи.
        if (add_go != null) {
            // Создание записи на основе данных из формы.
            Person new_person = new Person(request.getParameter("name"), request.getParameter("surname"), request.getParameter("middlename"));

            // Валидация ФИО.
            String error_message = this.validatePersonFMLName(new_person);

            // Если данные верные, можно производить добавление.
            if (error_message.equals("")) {

                // Если запись удалось добавить...
                if (this.phonebook.addPerson(new_person)) {
                    jsp_parameters.put("current_action_result", "ADDITION_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Добавление выполнено успешно");
                }
                // Если запись НЕ удалось добавить...
                else {
                    jsp_parameters.put("current_action_result", "ADDITION_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка добавления");
                }

                // Установка параметров JSP.
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_list.forward(request, response);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {
                // Подготовка параметров для JSP.
                jsp_parameters.put("current_action", "add");
                jsp_parameters.put("next_action", "add_go");
                jsp_parameters.put("next_action_label", "Добавить");
                jsp_parameters.put("error_message", error_message);

                // Установка параметров JSP.
                request.setAttribute("person", new_person);
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_manager.forward(request, response);
            }
        }

        if (add_phone_go != null) {
            String new_phone = request.getParameter("phonenumber");
            // Валидация номера.
            String error_message = this.validatePhone(new_phone);
            // Если номер верный, можно производить добавление.
            if (error_message.equals("")) {

                // Если запись удалось добавить...
                if (this.phonebook.addPhone(new_phone, id)) {
                    jsp_parameters.put("current_action_result", "ADDITION_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Добавление выполнено успешно");
                }
                // Если запись НЕ удалось добавить...
                else {
                    jsp_parameters.put("current_action_result", "ADDITION_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка добавления");
                }

                // Установка параметров JSP.
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.

                response.sendRedirect(request.getContextPath() + "/?action=edit&id=" + id);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {
                // Подготовка параметров для JSP.
                Person current_person = this.phonebook.getPerson(id);
                jsp_parameters.put("long_name", current_person.getLongName());
                jsp_parameters.put("current_action", "add_phone");
                jsp_parameters.put("next_action", "add_phone_go");
                jsp_parameters.put("next_action_label", "Добавить номер");
                jsp_parameters.put("error_message", error_message);

                // Установка параметров JSP.
                request.setAttribute("phone_number", new_phone);
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_phone_manager.forward(request, response);
            }
        }

        // Редактирование записи.
        if (edit_go != null) {

            // Валидация ФИО из формы.
            Person formPerson = new Person(request.getParameter("name"),
                    request.getParameter("surname"),
                    request.getParameter("middlename")
            );

            String error_message = this.validatePersonFMLName(formPerson);

            //Получение записи
            Person updatable_person = this.phonebook.getPerson(request.getParameter("id"));

            // Если данные верные, можно производить добавление.
            if (error_message.equals("")) {

                //Обновление записи на основе данных из формы.

                updatable_person.setName(request.getParameter("name"));
                updatable_person.setSurname(request.getParameter("surname"));
                updatable_person.setMiddlename(request.getParameter("middlename"));

                // Если запись удалось обновить...
                if (this.phonebook.updatePerson(id, updatable_person)) {
                    jsp_parameters.put("current_action_result", "UPDATE_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Обновление выполнено успешно");
                }
                // Если запись НЕ удалось обновить...
                else {
                    jsp_parameters.put("current_action_result", "UPDATE_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка обновления");
                }

                // Установка параметров JSP.
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_list.forward(request, response);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {

                // Подготовка параметров для JSP.
                jsp_parameters.put("current_action", "edit");
                jsp_parameters.put("next_action", "edit_go");
                jsp_parameters.put("next_action_label", "Сохранить");
                jsp_parameters.put("error_message", error_message);

                // Установка параметров JSP.
                request.setAttribute("person", updatable_person);
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_manager.forward(request, response);

            }
        }

        if (edit_phone_go != null) {

            // Валидация номера из формы.
            String formPhone = request.getParameter("phonenumber");

            String error_message = this.validatePhone(formPhone);

            // Если данные верные, можно производить добавление.
            if (error_message.equals("")) {

                // Если запись удалось обновить...
                if (this.phonebook.updatePhone(phoneid, formPhone, id)) {
                    jsp_parameters.put("current_action_result", "UPDATE_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Обновление выполнено успешно");
                }
                // Если запись НЕ удалось обновить...
                else {
                    jsp_parameters.put("current_action_result", "UPDATE_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка обновления");
                }

                // Установка параметров JSP.
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                response.sendRedirect(request.getContextPath() + "/?action=edit&id=" + id);
            }
//             Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {

                // Подготовка параметров для JSP.
                Person current_person = this.phonebook.getPerson(id);
                jsp_parameters.put("long_name", current_person.getLongName());
                jsp_parameters.put("current_action", "edit_phone");
                jsp_parameters.put("next_action", "edit_phone_go");
                jsp_parameters.put("next_action_label", "Сохранить номер");
                jsp_parameters.put("error_message", error_message);

                // Установка параметров JSP.
                request.setAttribute("phone_number", formPhone);
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_phone_manager.forward(request, response);

            }
        }
    }
}
