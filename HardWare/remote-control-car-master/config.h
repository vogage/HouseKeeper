#ifndef		__CONFIG_H
#define		__CONFIG_H

/*********************************************************/
//#define SYSTEM_CLOCK		22118400L	//定义主时钟
//#define SYSTEM_CLOCK		12000000L	//定义主时钟
#define SYSTEM_CLOCK		11059200L	//定义主时钟
//#define SYSTEM_CLOCK		 5529600L	//定义主时钟
//#define SYSTEM_CLOCK		24000000L	//定义主时钟
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

// 单核，进入/退出临界区，即关闭/开启全局中断
/*inline void critical_enter(void) {
	
}
inline void critical_exit(void) {
	
}*/


#endif
