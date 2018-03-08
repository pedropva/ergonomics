//
// Created by pedropva on 31/01/2018.
//

#ifndef ERGONOMICS_TEST_CLIENTSOCKET_H
#define ERGONOMICS_TEST_CLIENTSOCKET_H

#endif //ERGONOMICS_TEST_CLIENTSOCKET_H
// Definition of the ClientSocket class

#ifndef ClientSocket_class
#define ClientSocket_class

#include "Socket.h"


class ClientSocket : private Socket
{
public:

    ClientSocket ( std::string host, int port );
    virtual ~ClientSocket(){};

    const ClientSocket& operator << ( const std::string& ) const;
    const ClientSocket& operator >> ( std::string& ) const;

};


#endif