#include	"delay.h"

//========================================================================
// ����: void  delay_ms(unsigned char ms)
// ����: ��ʱ������
// ����: ms,Ҫ��ʱ��ms��, ����ֻ֧��1~255ms. �Զ���Ӧ��ʱ��.
// ����: none.
//========================================================================
void  delay_ms(unsigned char ms)
{
    unsigned int i;
    do{
        i = SYSTEM_CLOCK / 13000;
        while(--i)	;   //14T per loop
    }while(--ms);
}
