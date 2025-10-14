package kz.kaznu.course.lab2;

import java.time.Year;
import java.util.*;


public class Main
{
        //Оказывается чтобы что-то делать в static main, нужно делать это тоже static, поэтому все статичное такое
        //Массивы для рандомных функций(они снизу)
        static String[] titles =
        {
            "Legends", "Ketchup", "Cake shop by Daniil", "Orange", "Juice", "Dead Souls", "Lost", "Hollow mountain",
            "Fallen sky", "Crown", "Calm trees", "Abracadabra", "I'am tired boss..."
        };
        static String[] names =
        {
            "Daniil", "Amanjol", "DaniilSecond", "DannilTrird", "AgainDaniil?", "WhyNot?", "DaniilFouth",
            "Will Smit", "Johny Depp", "Danila Bagrov"
        };
        static String[] genres =
        {
            "Fantasy", "Deep fantasy", "Deep dark fantasy", "horror",
            "thriller", "science", "chess", "psy-horror", "DaniilHorror",
            "ifyouputsomeDaniilagain", "stop, please, god"
        };


    public static void main(String args[])
    {
        ArrayList<Item> library = new ArrayList<>();

        for(int i = 0; i < (new Random().nextInt(3, 5)); i++)
        {
            Book book = new Book(randTitle(), randName(), randYear(), randGenre(), 
                                new Random().nextInt(20, 500), true);
            library.add(book);
        }

        for(int i = 0; i < (new Random().nextInt(1, 4)); i++)
        {
            //Тестирую два конструктора
            Magazine magazineStaticIssue = new Magazine(randTitle(), randName(), randYear(),
                                             new Random().nextInt(20, 370), true);
            Magazine magazineNonStaticIssue = new Magazine(randTitle(), randName(), randYear(),
                                             new Random().nextInt(10000000, 100000010), new Random().nextInt(20, 365), true);
            library.add(magazineStaticIssue); 
            library.add(magazineNonStaticIssue);
        }

        for(int i = 0; i < (new Random().nextInt(3, 5)); i++)
        {
            DVD DVD = new DVD(randTitle(), randName(), randYear(), new Random().nextInt(10, 260),
                                "Director: " + randName(), true);
            library.add(DVD);
        }

        Collections.shuffle(library);

        System.out.printf("| %-9s | %-30s | %-30s | %4s | %-43s | %-11s |\n|%s|\n",
                          "Object", "Title", "Author", "Year", "Extra", "Available", "-".repeat(144) );

        for(Item item : library)
        {
            System.out.println(item);
        }
        for(Item item : library)
        {
            item.borrowItem();
            item.returnItem();
        }
        

    }

        public static String randTitle()
        {
            return titles[new Random().nextInt(titles.length)];
        }

        public static String randName()
        {
            return names[new Random().nextInt(names.length)];
        }

        public static String randGenre()
        {
            return genres[new Random().nextInt(genres.length)];
        }

        public static Year randYear()
        {
            return Year.of(new Random().nextInt(1732, 2025));
        }
}