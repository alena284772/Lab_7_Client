package ClientSide;

import Commands.*;
import Vehicle.*;


import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ClientConnection {
    private InetAddress inetAddress;
    private Scanner fromclient;
    private Boolean isConnected;
    private byte[] bytes = new byte[163840];
    Boolean isworking = true;
    private HistoryList historyList;
    private static ArrayList<String> files = new ArrayList<String>();
    SocketAddress socketAddress;
    private String login;
    private byte[] p;

    public ClientConnection() throws IOException, NoSuchAlgorithmException {
        isConnected = false;
        fromclient = new Scanner(System.in);
        historyList = new HistoryList(6);

        System.out.println("Enter a server address");
        String add = "";
        try {
            while (add == "") {
                String a = fromclient.nextLine();
                add = a;
                System.out.println("Server address is now: " + add);
            }
            System.out.println("Enter a port");
            int port = -1;
            while (port == -1) {
                try {
                    int p = Integer.valueOf(fromclient.nextLine().trim());
                    if (p < 0 || p > 65535) {
                        System.out.println("Wrong port was entered");
                    } else {
                        port = p;
                        System.out.println("Port is now: " + port);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entered value is not a number");
                }
            }
            socketAddress = new InetSocketAddress(add, port);
            boolean avtorization=true;
            String password;
            //MessageDigest md = MessageDigest.getInstance("MD5");
            //byte[] hash;
            while (avtorization) {
                System.out.println("Для началы работы нужна авторизация. Для авторизации введите avtor, для регистрации введите reg. Для выхода из приложения введите exit");
                String answer = fromclient.nextLine();
                Command command = new Command();
                switch (answer) {
                    case ("exit"):
                        System.exit(0);
                        break;
                    case ("avtor"):
                        command.setName("avtor");
                        System.out.println("Введите логин: ");
                        login = fromclient.nextLine().trim();
                        System.out.println("Введите пароль:");
                        password =  fromclient.nextLine().trim();
                        p = MessageDigest.getInstance("MD5").digest(password.getBytes());
                        command.setLogin(login);
                        command.setPassword(new String(p));
                        if (this.sendMessage(command).getAnswer().equals("Авторизация прошла успешно.")) {
                            avtorization=false;
                            break;
                        }
                        else {
                            continue;
                        }
                    case ("reg"):
                        command.setName("reg");
                        System.out.println("Введите логин:");
                        login = fromclient.nextLine().trim();
                        System.out.println("Введите пароль:");
                        password = fromclient.nextLine().trim();
                        p = MessageDigest.getInstance("MD5").digest(password.getBytes());
                        command.setLogin(login);
                        command.setPassword(new String(p));
                        if (this.sendMessage(command).getAnswer().equals("Пользователь добавлен")) {
                            avtorization=false;
                            break;
                        }
                        else {
                            continue;
                        }

                }
            }

            System.out.println("You can start working");

        }catch (NoSuchElementException e){
            System.out.println("End of input");
            System.exit(0);

        }

    }

    public void start() throws IOException {

        while (isworking) {
            work(fromclient);


        }
    }


    public void work( Scanner scanner) throws IOException {
        while (isworking && scanner.hasNext()) {
            String clientcom = scanner.nextLine();
            String[] input_ar = clientcom.split(" ");
            switch (input_ar[0]) {

                case ("help"):
                    help(historyList);
                    break;

                case ("info"):
                    Command command2 = new Command("info");
                    command2.setLogin(login);
                    command2.setPassword(new String(p));
                    this.sendMessage(command2);
                    historyList.insert("info");
                    break;

                case ("show"):
                    Command command3 = new Command("show");
                    command3.setLogin(login);
                    command3.setPassword(new String(p));
                    this.sendMessage(command3).getAnswer();
                    historyList.insert("show");
                    break;

                case ("insert"):
                    try {
                        Integer Key = Key_check(input_ar[1], fromclient);
                        Vehicle vehicle = new Vehicle(fromclient);
                        vehicle.setUser(login);
                        Command command4 = new Command("insert");
                        command4.setKey(Key);
                        command4.setVehicle(vehicle);
                        command4.setLogin(login);
                        command4.setPassword(new String(p));
                        this.sendMessage(command4);
                        historyList.insert("insert");
                        break;}catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("The arguments to the command are incorrect. Use the help command");
                        break;
                    }

                case ("update"):
                    try {
                        Long ID = Long_check(input_ar[1], fromclient);
                        Vehicle vehicle1 = new Vehicle(fromclient);
                        vehicle1.setUser(login);
                        Command command5 = new Command("update");
                        command5.setID(ID);
                        command5.setVehicle(vehicle1);
                        command5.setLogin(login);
                        command5.setPassword(new String(p));
                        this.sendMessage(command5);
                        historyList.insert("update");
                        break;}catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("The arguments to the command are incorrect. Use the help command");
                        break;
                    }

                case ("remove_key"):
                    try{
                        Integer key = Key_check(input_ar[1], fromclient);
                        Command command6 = new Command("remove_key");
                        command6.setKey(key);
                        command6.setLogin(login);
                        command6.setPassword(new String(p));
                        this.sendMessage(command6);
                        historyList.insert("remove_key");
                        break;}catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("The arguments to the command are incorrect. Use the help command");
                        break;
                    }

                case ("clear"):
                    Command command7 = new Command("clear");
                    command7.setLogin(login);
                    command7.setPassword(new String(p));
                    this.sendMessage(command7);
                    historyList.insert("clear");
                    break;

                case ("execute_script"):
                    try{
                        read_script(input_ar[1],historyList);
                        historyList.insert("execute_script");
                        break;}catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("The arguments to the command are incorrect. Use the help command");
                        break;
                    }

                case ("exit"):
                    this.isworking = false;
                    break;

                case ("remove_greater"):
                    Vehicle vehicle2 = new Vehicle(fromclient);
                    vehicle2.setUser(login);
                    Command command10 = new Command("remove_greater");
                    command10.setVehicle(vehicle2);
                    command10.setLogin(login);
                    command10.setPassword(new String(p));
                    this.sendMessage(command10);
                    historyList.insert("remove_greater");
                    break;

                case ("history"):
                    historyList.show();
                    historyList.insert("history");
                    break;

                case ("replace_if_greater"):
                    try{
                        Integer Key1 = Key_check(input_ar[1], fromclient);
                        Vehicle vehicle3 = new Vehicle(fromclient);
                        vehicle3.setUser(login);
                        Command command12 = new Command("replace_if_greater");
                        command12.setKey(Key1);
                        command12.setVehicle(vehicle3);
                        command12.setLogin(login);
                        command12.setPassword(new String(p));
                        this.sendMessage(command12);
                        historyList.insert("replace_if_greater");
                        break;}catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("The arguments to the command are incorrect. Use the help command");
                        break;
                    }

                case ("remove_any_by_number_of_wheels"):
                    try{
                        Long number = Long_check(input_ar[1], fromclient);
                        Command command13 = new Command("remove_any_by_number_of_wheels");
                        command13.setNumber(number);
                        command13.setLogin(login);
                        command13.setPassword(new String(p));
                        this.sendMessage(command13);
                        historyList.insert("remove_any_by_number_of_wheels");
                        break;}catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("The arguments to the command are incorrect. Use the help command");
                        break;
                    }

                case ("count_less_than_engine_power"):
                    try{
                        Float power = Float_check(input_ar[1], fromclient);
                        Command command14 = new Command("count_less_than_engine_power");
                        command14.setPower(power);
                        command14.setLogin(login);
                        command14.setPassword(new String(p));
                        this.sendMessage(command14);
                        historyList.insert("count_less_than_engine_power");
                        break;}catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("The arguments to the command are incorrect. Use the help command");
                        break;
                    }
                case ("print_field_ascending_type"):
                    List<VehicleType> VehT = Arrays.asList(VehicleType.values());
                    Collections.sort(VehT);
                    System.out.println(VehT);
                    historyList.insert(input_ar[0]);
                    break;

                default:
                    System.out.println(input_ar[0]);
                    System.out.println("Incorrect command. Try again");
                    help(historyList);

            }
            continue;

        }
    }

    public void help(HistoryList historyList) {
        System.out.println("help: вывести справку по доступным командам");
        System.out.println("info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        System.out.println("show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        System.out.println("insert key {element}: добавить новый элемент с заданным ключом");
        System.out.println("update id {element}: обновить значение элемента коллекции, id которого равен заданному");
        System.out.println("remove_key key: удалить элемент из коллекции по его ключу");
        System.out.println("clear: очистить коллекцию");
        System.out.println("execute_script file_name: считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
        System.out.println("exit: завершить программу ");
        System.out.println("remove_greater {element}: удалить из коллекции все элементы, превышающие заданный");
        System.out.println("history: вывести последние 6 команд(без их аргументов)");
        System.out.println("replace_if_greater key {element}: заменить значение по ключу, если новое значение больше старого");
        System.out.println("remove_any_by_number_of_wheels numberOfWheels:удалить из коллекции один элемент, значение поля numberOfWheels которого эквивалентно заданному");
        System.out.println("count_less_than_engine_power: вывести количество элементов, значение поля enginePower которых меньше заданного");
        System.out.println("print_field_ascending_type: вывести значения поля type в порядке возрастания");
        historyList.insert("help");
    }

    public Command wait_answer(SocketChannel outcoming) throws IOException {
        Command command00=new Command();
        System.out.println("Answer from server:");
        while(command00.getAnswer()==null&&isworking) {
            try {
                Thread.sleep(1000);
                command00.setAnswer(this.read_answer(outcoming).getAnswer());
                if(command00.getAnswer()==null){
                    break;
                }else{
                    System.out.println(command00.getAnswer());
                }
            } catch (IOException e) {
                //System.out.println("Server did not send an answer");
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                isworking=false;
                System.out.println("Server is closed");
            }
        }

        return command00;
    }

    public Command sendMessage(Command command) throws IOException {
        SocketChannel outcoming = null;
        boolean canconnect=true;
        try {
            outcoming = SocketChannel.open(socketAddress);
            outcoming.configureBlocking(false);

        System.out.println("Connection established");
        ByteBuffer sendBuffer = ByteBuffer.allocate(16384);
        try (

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            objectOutputStream.writeObject(command);

            sendBuffer.put(byteArrayOutputStream.toByteArray());

            objectOutputStream.flush();
            byteArrayOutputStream.flush();
            sendBuffer.flip();
            outcoming.write(sendBuffer);

            System.out.println("----\nMessage sent.\n----");

            objectOutputStream.close();

            byteArrayOutputStream.close();
            sendBuffer.clear();



        } catch (IOException e) {

        }
        } catch (IOException e) {
            System.out.println("В данный момент нет доступа к серверу. Попробуйте сделать запрос позже");
            canconnect=false;
        }
        //wait_answer(outcoming);
        if(canconnect){
        return wait_answer(outcoming);}else{
            Command no=new Command();
            no.setAnswer("Сервер недоступен");
            return no;
        }

    }

    public Command read_answer(SocketChannel socketChannel) throws IOException {
        Command command = new Command();
        ByteBuffer readBuffer = ByteBuffer.allocate(163840);
        try {
            socketChannel.read(readBuffer);
            readBuffer.flip();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(readBuffer.array());
            readBuffer.flip();
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Command command1=(Command)objectInputStream.readObject();
            command.setAnswer(command1.getAnswer());
            //System.out.println(command.getAnswer());
            objectInputStream.close();
            byteArrayInputStream.close();
            readBuffer.clear();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Some problem with server. Try again");
            //e.printStackTrace();
        }

        return command;
    }

    public Integer Key_check(String string, Scanner scanner) {
        Integer Key = null;
        try {
            Key = Integer.valueOf(string);
        } catch (NumberFormatException E) {
            System.out.println("Input arg of key is incorrect.It must be Integer value. Try again");
            while (Key == null) {
                System.out.println("Key:");
                try {
                    Key = Integer.valueOf(scanner.next());
                } catch (NumberFormatException e) {
                    System.out.println("Input arg of key is incorrect. Try again");
                }
            }
        }
        return Key;
    }

    public Long Long_check(String string, Scanner scanner) {
        Long aLong = null;
        try {
            aLong = Long.valueOf(string);
        } catch (NumberFormatException E) {
            System.out.println("Input arg of long value is incorrect. Try again");
            while (aLong == null) {
                System.out.println("Long value:");
                try {
                    aLong = Long.valueOf(scanner.next());
                } catch (NumberFormatException e) {
                    System.out.println("Input arg of long value is incorrect. Try again");
                }
            }
        }
        return aLong;
    }

    public Float Float_check(String string, Scanner scanner) {
        Float aFloat = null;
        try {
            aFloat = Float.valueOf(string);
        } catch (NumberFormatException E) {
            System.out.println("Input arg of float value is incorrect. Try again");
            while (aFloat == null) {
                System.out.println("Float value:");
                try {
                    aFloat = Float.valueOf(scanner.next());
                } catch (NumberFormatException e) {
                    System.out.println("Input arg of float value is incorrect. Try again");
                }
            }
        }
        return aFloat;
    }


    public void read_script(String string, HistoryList historyList) throws IOException {
        if (files.contains(string) == false) {
            files.add(string);
            File file1 = new File(string);
            try {
                Scanner scan = new Scanner(file1);
                while (scan.hasNextLine()) {
                    work(scan);
                }
                scan.close();
            } catch (FileNotFoundException|SecurityException e) {
                System.out.println("Some problems with file");
                System.out.println("Try again");

            }
            historyList.insert("Execute_script");
        } else {
            System.out.println("The file specified in the execute method is already used, you cannot use it again");
        }

        files.clear();

    }

}