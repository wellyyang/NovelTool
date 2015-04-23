

/**

 * todo list:
 * 增加对收藏作者的检索
 * <br>
 * 1.0 released at 2014.04.03<br>
 * 		初版,检索指定目录下所有符合规定格式的txt,按作者或类型分类显示.<br>
 * 2.0 released at 2014.04.18<br>
 * 		将所有数据,包括txt文件内容保存到sqlite数据库,本地文件仅作为导入使用.<br>
 * 2.1 released at 2014.04.30<br>
 *  	1.可以在导入时指定该文件本来的时间<br>
 *  	2.自动删除数据库中重复文件内容.<br>
 *  	3.增加根据书名检索<br>
 *  	4.可以手动删除文件.<br>
 * 2.2 released at 2014.05.08:<br>
 * 		1.修改检索硬盘文件使用ForkJoinTask<br>
 * 		2.增加修改标签功能<br>
 * 2.3 released at 2014.05.12:<br>
 * 		1.增加删除功能<br>
 * 		2.修正部分bug<br>
 * 2.4 released at 2015.04.23:<br>
 * 		1.增加收藏作者功能<br>
 * 		2.处理检索文件可能会出现空指针问题<br>
 * 		3.可以双击取消评分<br>
 * 
 * @author Welly Yang
*/
package com.welly.noveltool;