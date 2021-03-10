#include <iostream> //входной/выходной поток
#include <dirent.h> //итерация по директориям
#include <cerrno> //вывод ошибок
#include <vector>
#include <cstring> //перевод в Си-строку
#include <fstream> //файловый поток
#include <random> //рандомный генератор
#include <functional> // эти   для          рандомной                   bind
#include <algorithm> //     два    генерации          последовательности    generate
#include <cstdio>
#include <map> //для хэш-таблицы

typedef void(*func)(std::string&, std::vector<std::string>&); // переименованный указатель на функцию, которая принимает ссылку на строку и вектор строк

void userEntrance(std::string&, std::vector<std::string>& path) //пользовательский ввод
{
    int n = 0;
    std::cin >> n;
    if( n < 0 || n >= path.size() ) //если номер файла для перезаписи выходит за границы массива
    {
        std::cout << "Out of Range\n"; //ошибка
        return;
    }

    std::fstream file(path[n], std::ios::in | std::ios::out | std::ios::binary | std::ios::ate); // открываем с флагами: входной, выходной (потоки), бинарный файл, каретка в конце
    if( !file.good() ) // если открылся на правильно
        std::cout << "\nErr\n";
    else
    {
        size_t size = file.tellg(); //кполучаем размер файла
        file.seekg(0, std::ios::beg); // возвращаем каретку в начало
        std::string bufseq; //для записи последователньости
        int num = 0;
        std::cin >> num; //считываем кол-во последовательностей
        if( num < 0 )
        {
            std::cout << "Wrong number of cycles";
            return;
        }

        for( int i = 0; i < num; ++i )
        {
            std::cin >> bufseq; //считываем последовательность
            if( bufseq.empty() )
            {
                std::cout << "Empty sequence\n";
            }
            std::vector<char> seq(bufseq.begin(), bufseq.end());

			//Блок который подгоняет размеры входной последователньости под размер файла
			///////////////////
			
            if( seq.size() < size )
            {
                size_t lpos = 0;
                while( seq.size() < size )
                {
                    seq.insert(seq.end(), seq.begin() + lpos, seq.begin() + lpos + size - seq.size());
                    lpos += size - seq.size();
                }

            }
			
			///////////////////
			
            std::cout << "Writing " << i + 1 << " user sequence to the file... ";
            file.write((char*)&seq[0], size); //записываем в файл к Си строку
            std::cout << "Done\n";
            file.seekg(0, std::ios::beg); //перемещаем каретку в начало
            seq.clear(); //чистим массив
        }

//        if( remove(path[n].c_str()) != 0 )
//            perror( "Error deleting file" );
//        else
//            puts( "File successfully deleted" );
    }
}

void digging(std::string& path, std::vector<std::string>& paths) //обход директорий
{
    DIR* cur; // директория, в который мы сейчас
    dirent* end; // то, что будет считываться
    if( (cur = opendir(path.c_str())) ) //открываем её
    {
        while( (end = readdir(cur)) ) //до тех пор пока не считали весь список файлов внутри директории
        {
            if( std::string(end->d_name).find('.') == std::string::npos )
            {
                std::string some = path + "\\" + end->d_name; //если считали директорию, то открываем и считываем файлы внутри
                digging(some, paths);
            }

            if( strcmp(end->d_name, ".") && strcmp(end->d_name, "..") ) //иначе записываем путь к файлу в массив
                paths.push_back( path  + "\\" + end->d_name );
        }
        closedir(cur); //не забываем закрыть директорию
    }
}

void setPath(std::string& defpath, std::vector<std::string>& paths) //выбор другого пути
{
    std::string buffpath;
    std::cin >> buffpath; //считываем

    if( buffpath.find('.') == std::string::npos )
    {
        defpath = buffpath; //если директория - начинаем с неё обход
        digging(defpath, paths);
    }
    else
    {
        paths.push_back(buffpath); // иначе кладём имя файла в массив
        size_t p = buffpath.find_last_of("\\");
        buffpath.erase(p);
        defpath = buffpath;
    }
    if( errno ) //если возникла ошибка - выводим
    {
        std::cout << std::strerror(errno) << "...\n";
        setPath(defpath, paths);
    }
}

void showFiles(std::string&, std::vector<std::string>& paths) // вывод списка файлов с индексом
{
    for( size_t i = 0; i < paths.size(); ++ i )
        std::cout << "  " << i << " - " << paths[i].substr(paths[i].find_last_of('\\') + 1) << '\n';
    std::cout << '\n';
}

void readSelected(std::string& path, std::vector<std::string>& paths) //считывание файла
{
    std::ifstream file;
    int num;
    std::cin >> num;
    if( num < 0 || num > paths.size() ) //проверка индекса
    {
        std::cout << "Out of range\n";
        return;
    }
    file.open(paths[num]); //открываем файл
    for( std::string line; std::getline( file, line ); ) //построчно выводим в консоль
    {
        std::cout << line << '\n';
    }
    file.close(); //не забываем закрыть :D
}

void help(std::string&, std::vector<std::string>&) //вывод менюхи
{
    std::cout << "PATH <absolute path> - change file search path\n"
                 "LIST - show files in search path\n"
                 "READ <file num> - read file with selected number\n"
                 "ERASE <file number> - wipe file with selected number from 'Show files'\n"
                 "using DoD 5220.22-M\n"
                 "ABOUT - information about algorithm\n"
                 "ENTSEQ <file number> <cycle number> <0>..<n> - fill filewith user-typed sequence\n";
}

bool comp( std::vector<char> seq, std::fstream& file ) //сравнение записанной и сгенерированной последовательности
{
    std::vector<char> ver;
    ver.resize(seq.size());

    std::cout << "Verifying...................";
    file.read((char*)&ver[0], seq.size()); //считываем в буферный массив
    if( ver != seq ) // последовательности разные - ошибка
    {
        std::cout << "Failed";
        return false;
    }
    std::cout << "Done\n\n";
    file.seekg(0, std::ios::beg); //возвращаем каретку в начало
    return true;
}

void rewriteFile(std::fstream& file) //перезеапись файла алгоритмом
{
    size_t size = file.tellg();
    file.seekg(0, std::ios::beg);
    std::vector<char> seq(size); //считываем размеры файла и делаем массив того же размера

    //Zeros
    seq.assign(size, 0); // записываем нули в массив

    std::cout << "Filling with zeros..........";
    file.write((char*)&seq[0],size); //записываем в файл
    std::cout << "Done\n";
    file.seekg(0, std::ios::beg);

    if( !comp(seq, file) ) return; //проверяем что последовательности совпали

	//(следующая такая же только 1 вместо 0)
	
    //Ones
    seq.assign(size, 1);

    std::cout << "Filling with ones...........";
    file.write((char*)&seq[0], size);
    std::cout << "Done\n";
    file.seekg(0, std::ios::beg);

    if( !comp(seq, file) ) return;

    //Rando
    std::random_device rnd_device; //получаем зерно
    std::mt19937 mt(rnd_device()); //запускаем выборку
    std::uniform_int_distribution<int> b(0, 1); //ограничиваем последовательность от 0 до 1
    auto gen = std::bind(b, mt);//делаем функцию, которая будет возвращать 0/1
    std::generate(seq.begin(), seq.end(), gen); //записываем случайную последовательность в массив

    std::cout << "Filling with random bytes...";
    file.write((char*)&seq[0], size); //записываем в файл
    std::cout << "Done\n";
    file.seekg(0, std::ios::beg);

    if( !comp(seq, file) ) return; //сверяем

}

void dod5220(std::string&, std::vector<std::string>& paths) //инициализация перезаписи
{
    int num;
    std::cin >> num;
    if( num < 0 || num > paths.size() ) //проверка на выход за границы
    {
        std::cout << "Out of range\n";
        return;
    }

    std::fstream file(paths[num], std::ios::in | std::ios::out | std::ios::binary | std::ios::ate); //// открываем с флагами: входной, выходной (потоки), бинарный файл, каретка в конце
    if( !file.good() )
    {
        std::cout << "Can't open file"; //если не смогли открыть
        return;
    }
    rewriteFile(file); //перезаписываем
}

void about(std::string&, std::vector<std::string>&) //вывод информации об алгоритме
{
    std::cout << "DoD 5220.22-M sanitization algorithm can be devided into 3 parts:\n"
                 "Pass 1: Writes a zero and verifies the write\n"
                 "Pass 2: Writes a one and verifies the write\n"
                 "Pass 3: Writes a random character and verifies the write\n";
}

int main(void)
{
    setlocale(LC_ALL, "Russian"); //русский язык
    std::string menu; //переменная для считывания команд
    std::string defpath; //путь, который укажет пользователь
    std::vector<std::string> paths; //пути к файлам
    std::map<std::string, func> strToFunc; //контейнер для ассоциации строки и функции

	//Всё что ниже кладёт в контейнер (строка, указатель на функцию)
    strToFunc.insert(std::make_pair("/HELP", &help));
    strToFunc.insert(std::make_pair("LIST", &showFiles));
    strToFunc.insert(std::make_pair("ERASE", &dod5220));
    strToFunc.insert(std::make_pair("PATH", &setPath));
    strToFunc.insert(std::make_pair("ABOUT", &about));
    strToFunc.insert(std::make_pair("ENTSEQ", &userEntrance));
    strToFunc.insert(std::make_pair("READ", &readSelected));

    while( true ) //бесконечно
    {
        std::cin >> menu; //считываем команду
        if( strToFunc.find(menu) == strToFunc.end() )//если такой нет - выводим подсказку
        {
            std::cout << "Command not found. Enter '/HELP' to get list of available commands\n";
            continue;
        }
        strToFunc[menu](defpath, paths);//иначе вызываем нужную функцию
    }
    return 0;
}