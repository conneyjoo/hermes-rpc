/**
 * Created by louis on 2020/9/29.
 */
package com.xhtech.hermes.core.util;

import org.springframework.util.StopWatch;

/**
 * com.xhtech.hermes.core.util.StopWatchUtil
 *
 * @author: louis
 * @time: 2020/9/29 4:03 PM
 */
public class StopWatchUtil {
    public static String parseStopWatch(StopWatch stopWatch){
        StringBuilder sb = new StringBuilder();
        for(StopWatch.TaskInfo taskInfo : stopWatch.getTaskInfo()){
            sb.append("[");
            sb.append(taskInfo.getTaskName());
            sb.append("]:");
            sb.append(taskInfo.getTimeSeconds());
            sb.append("秒;");
        }
        return sb.toString();
    }
}
