package cn.shiva.service;

import cn.shiva.entity.FileRecovery;
import cn.shiva.entity.NovelFile;
import cn.shiva.mapper.FileRecoveryMapper;
import cn.shiva.mapper.NovelFileMapper;
import cn.shiva.mapper.NovelLabelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件相关服务
 *
 * @author shiva   2023-12-26 9:07
 */
@Slf4j
@Service
public class NovelService {

    @Autowired
    private NovelFileMapper novelFileMapper;
    @Autowired
    private AliOssComponent ossComponent;
    @Autowired
    private FileRecoveryMapper recoveryMapper;
    @Autowired
    private NovelLabelMapper labelMapper;

    /**
     * 移动OSS文件；删除本地novelFile文件；新增回收站文件
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deleteNovel(NovelFile novel) {
        //复制到旧版
        FileRecovery recovery = new FileRecovery();
        BeanUtils.copyProperties(novel, recovery);
        recovery.setId(null);
        recovery.setOssPath(recovery.getOssPath().replace("novel/", "recovery/"));
        recovery.setFilePath(recovery.getFilePath().replace("novel/", "recovery/"));
        //sqlite修改
        recoveryMapper.insert(recovery);
        novelFileMapper.deleteById(novel);
        //中间表的标签要删除；
        labelMapper.deleteByNovelId(novel.getId());
        //OSS移动
        ossComponent.moveObject(novel.getOssPath(), recovery.getOssPath());
    }

}
