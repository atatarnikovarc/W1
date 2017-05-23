/* 
 * File:   Cookie.cpp
 * Author: kogorodnikov
 * 
 * Created on July 12, 2010, 2:13 PM
 */

#include "string.h"
#include "Cookie.h"
#include "stdio.h"

Cookie::Cookie() {
    this->cookie.clear();
    this->cookie_list.clear();
}

Cookie::Cookie(const Cookie& orig) {
}

Cookie::~Cookie() {
}

static int getNextPair(char** cur_pp, char** pp_end, cookie_list_node* pln)
{
    char* p1 = NULL;
    char* p2 = NULL;

    // first time calling
    if (!(*pp_end)) {
        p1 = strstr(*cur_pp, "Set-Cookie: ");
        if (!p1)
            return 0;
        p1 += strlen("Set-Cookie: ");        
        *pp_end = strstr(p1, "\r\n");
        if (!(*pp_end))
            return 0;
    } else {
        p1 = *cur_pp;
    }

    p2 = strchr(p1, '=');
    if (!p2 || p2 >= *pp_end)
        return 0;
    pln->name.assign(p1, p2 - p1);
    p1 = p2 + 1;
    p2 = strchr(p1, ';');
    if (!p2 || p2 >= *pp_end)
        return 0;
    pln->value.assign(p1, p2 - p1);

    // move to the next record
    p1 = p2 + 2;
    *cur_pp = p1;
    p2 = strchr(p1, '=');
    if (!p2 || p2 >= *pp_end) {
        *pp_end = NULL;
        return 1;
    }
    
    if (p2 - p1 == 7 /*strlen("expires")*/) {
        if (strncasecmp(p1, "expires", p2 - p1) == 0) {
            *pp_end = NULL;
        }
    } else if (p2 - p1 == 4 /*strlen("path")*/) {
        if (strncasecmp(p1, "path", p2 - p1) == 0) {
            *pp_end = NULL;
        }
    } else if (p2 - p1 == 6 /*strlen("domain")*/) {
        if (strncasecmp(p1, "domain", p2 - p1) == 0) {
            *pp_end = NULL;
        }
    }

    return 1;

}

void Cookie::generateCookieString()
{
    list<cookie_list_node>::iterator i;
    if (cookie_list.empty()) {
        cookie.clear();
        return;
    }
    cookie.assign("Cookie: ");
    for (i = cookie_list.begin(); i != cookie_list.end(); ++i) {
        cookie.append(i->name);
        cookie.append("=");
        cookie.append(i->value);
        cookie.append("; ");
    }
    cookie.append("\r\n");
}

void Cookie::update(char* buf)
{
    char* p = buf;
    char* p_end = NULL;
    int found = 0;
    int modified = 0;
    list<cookie_list_node>::iterator i;
    cookie_list_node cookie_ln;
    while (getNextPair(&p,  &p_end, &cookie_ln)) {
        found = 0;
        for (i = this->cookie_list.begin(); i != this->cookie_list.end(); ++i) {
            if (i->name.compare(cookie_ln.name) == 0) {
                found = 1;
                break;
            }
        }
        if (found) {
            if (i->value.compare(cookie_ln.value) != 0) {
                if ((i->name.compare("o") == 0) ||
                    (i->name.compare("O") == 0) ||
                    (i->name.compare("u") == 0) ||
                    (i->name.compare("U") == 0)) {
                    printf("error: The value for cookie %s has changed from %s to %s!!!\r\n",
                            i->name.c_str(), i->value.c_str(), cookie_ln.value.c_str());
                }
                i->value.assign(cookie_ln.value);
                modified = 1;
            }
        } else {
            this->cookie_list.push_back(cookie_ln);
            modified = 1;
        }
    }
    if (modified)
        generateCookieString();
}

