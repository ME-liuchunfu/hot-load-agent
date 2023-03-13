package xin.spring.hotload;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;

/**
 * 热更新服务器
 *
 * @author spring
 * @date 2023/03/09
 */
public class HotUpdateServer {

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        if (args != null && args.length >= 3) {
            String agentJar = args[0];
            String[] pidArr = args[1].split(",");
            for (int i = 0; i < pidArr.length; i++) {
                String pid = pidArr[i];
                VirtualMachine vm = VirtualMachine.attach(pid);
                System.out.println("正在热更新的pid是:" + pid);
                vm.loadAgent(agentJar, args[2]);
            }
        } else {
            System.out.println("至少需要AgentJar包路径和一个进程id！！！");
        }
    }
}
