# Python Program to Get IPv4 Address

import socket

hostname = socket.gethostname()
IPAddr = socket.gethostbyname(hostname)
 
print("Your Computer Name is:" + hostname)
print("Your Computer IP Address is:" + IPAddr)


import stun
nat_type, external_ip, external_port = stun.get_ip_info()

print(nat_type)
print(external_ip)
print(external_port)
#Get IPv6 Address
IPv6Addr=socket.getaddrinfo(hostname,None,socket.AF_INET6)
print("Your Computer IPv6 Address is")
#print(IPv6Addr)
#[(<AddressFamily.AF_INET6: 30>, <SocketKind.SOCK_DGRAM: 2>, 17, '', 
#  ('::ffff:192.168.137.248', 0, 0, 0)), (<AddressFamily.AF_INET6: 30>, 
#<SocketKind.SOCK_STREAM: 1>, 6, '', ('::ffff:192.168.137.248', 0, 0, 0))]



