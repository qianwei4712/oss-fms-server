package cn.shiva.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 文件相关服务
 *
 * @author shiva   2023-12-26 9:07
 */
@Slf4j
@Service
public class NovelService {

    /**
     * 删除文件或者文件夹，进入回收站;
     * 1.区分文件和文件夹
     * 2.软删除，就是移动到回收站；
     * 3.原有的标签全部清空
     */
    public void delete(Long novelId) {


    }

}
