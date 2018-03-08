//
// Created by pedropva on 31/01/2018.
//

#include "ClientSocket.h"
// Implementation of the ClientSocket class

#include "ClientSocket.h"
#include "SocketException.cpp"
#include <cerrno>
#include <sstream>

#define SSTR( x ) static_cast< std::ostringstream & >( \
        ( std::ostringstream() << std::dec << x ) ).str()

ClientSocket::ClientSocket ( std::string host, int port )
{
    if ( ! Socket::create() )
    {
        std::string errorNumber = SSTR(errno);
        throw SocketException ( "Could not create client socket:"+ errorNumber );
    }

    if ( ! Socket::connect ( host, port ) )
    {
        std::string errorNumber = SSTR(errno);
        throw SocketException ( "Could not bind to port:"+ errorNumber );
    }

}


const ClientSocket& ClientSocket::operator << ( const std::string& s ) const
{
    if ( ! Socket::send ( s ) )
    {
        throw SocketException ( "Could not write to socket." );
    }

    return *this;

}


const ClientSocket& ClientSocket::operator >> ( std::string& s ) const
{
    if ( ! Socket::recv ( s ) )
    {
        throw SocketException ( "Could not read from socket." );
    }

    return *this;
}