package cn.shiva.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shiva   2023-12-24 23:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadBO {
    private String name;
    private String content;
}
