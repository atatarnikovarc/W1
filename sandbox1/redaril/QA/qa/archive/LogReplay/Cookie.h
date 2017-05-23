/* 
 * File:   Cookie.h
 * Author: kogorodnikov
 *
 * Created on July 12, 2010, 2:13 PM
 */

#ifndef _COOKIE_H
#define	_COOKIE_H

#include <list>
#include <string>
using namespace std;

typedef struct _cookie_list_node {
    string name;
    string value;
} cookie_list_node;

class Cookie {
public:
    Cookie();
    Cookie(const Cookie& orig);
    virtual ~Cookie();
    void update(char* buf);

    string cookie;
private:
   list<cookie_list_node> cookie_list;
   void generateCookieString();
};

#endif	/* _COOKIE_H */

