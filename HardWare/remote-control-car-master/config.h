#ifndef		__CONFIG_H
#define		__CONFIG_H

/*********************************************************/
//#define SYSTEM_CLOCK		22118400L	//������ʱ��
//#define SYSTEM_CLOCK		12000000L	//������ʱ��
#define SYSTEM_CLOCK		11059200L	//������ʱ��
//#define SYSTEM_CLOCK		 5529600L	//������ʱ��
//#define SYSTEM_CLOCK		24000000L	//������ʱ��
/*********************************************************/

#include	"STC8G.H"

#ifndef NULL
#define NULL        0u
#endif

#ifndef NULL_PTR
#define NULL_PTR    0u
#endif

typedef 	unsigned char	u8;
typedef 	unsigned int	u16;
typedef 	unsigned long	u32;

// ���ˣ�����/�˳��ٽ��������ر�/����ȫ���ж�
/*inline void critical_enter(void) {
	
}
inline void critical_exit(void) {
	
}*/


#endif
