//
// Created by pedropva on 31/01/2018.
//

#ifndef ERGONOMICS_TEST_SOCKETEXCEPTION_H
#define ERGONOMICS_TEST_SOCKETEXCEPTION_H

#endif //ERGONOMICS_TEST_SOCKETEXCEPTION_H

// SocketException class


#ifndef SocketException_class
#define SocketException_class

#include <string>

class SocketException
{
public:
    SocketException ( std::string s ) : m_s ( s ) {};
    ~SocketException (){};

    std::string description() { return m_s; }

private:

    std::string m_s;

};

#endif