package cn.shiva.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shiva   2024-01-05 9:28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OssParams {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String areaSuffix;

}
