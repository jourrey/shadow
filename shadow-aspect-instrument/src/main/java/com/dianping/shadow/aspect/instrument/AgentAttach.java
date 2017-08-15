package com.dianping.shadow.aspect.instrument;

import com.dianping.shadow.util.RootPathUtils;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.util.Scanner;

/**
 * Created by jourrey on 17/2/23.
 * 在服务启动后,使用Attach加入代理
 * 执行命令:
 * <code>
 * java -cp /Users/../shadow-1.1.0-SNAPSHOT.jar com.dianping.shadow.aspect.instrument.AgentAttach
 * </code>
 */
public class AgentAttach {

    /**
     * 通过程序捆绑的代码：<br/>
     * <code>
     * VirtualMachine vm=VirtualMachine.attach("PID"); //给指定的进程捆绑agent<br/>
     * 在得到目标进程的vm后，就可以通过
     * vm.loadAgent("agent.jar"),vm.loadAgentLibrary(dll), and loadAgentPath(dllPath) 进行捆绑操作了 <br/>
     * 其中:<br>
     * loadAgent是捆绑一个jar文件，
     * loadAgentLibrary,loadAgentPath则是捆绑本地方法库（动态连接库）
     * </code>
     *
     * @param args
     * @throws AttachNotSupportedException
     * @throws IOException
     * @throws AgentLoadException
     * @throws AgentInitializationException
     */
    public static void main(String[] args) throws AttachNotSupportedException,
            IOException, AgentLoadException, AgentInitializationException {
        String pid;
        if (args == null || args.length == 0) {
            System.out.println("Pid - The abstract identifier that identifies the Java virtual machine.");
            System.out.print("Please enter pid : ");
            Scanner s = new Scanner(System.in);
            pid = s.next();
        } else {
            pid = args[0];
        }
        System.out.println(MessageFormat.format("pid:{0}", pid));

        VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent(RootPathUtils.getRootAbsolutePath());
        vm.detach();
    }

    /**
     * 返回当前进程PID
     *
     * @return
     */
    public static String getCurrentPid() {
        // get name representing the running Java virtual machine.
        String name = ManagementFactory.getRuntimeMXBean().getName();
        // get pid
        String pid = name.split("@")[0];
        return pid;
    }

}
