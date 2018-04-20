package com.dturan.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import javax.validation.constraints.*;
/**
 * Информация о голосовании пользователя. 
 */
@ApiModel(description = "Информация о голосовании пользователя. ")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

public class Vote   {
  @JsonProperty("nickname")
  private String nickname = null;

  @JsonProperty("voice")
  private BigDecimal voice = null;

  public Vote nickname(String nickname) {
    this.nickname = nickname;
    return this;
  }

   /**
   * Идентификатор пользователя.
   * @return nickname
  **/
  @ApiModelProperty(required = true, value = "Идентификатор пользователя.")
  @NotNull
  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public Vote voice(BigDecimal voice) {
    this.voice = voice;
    return this;
  }

   /**
   * Отданный голос.
   * @return voice
  **/
  @ApiModelProperty(required = true, value = "Отданный голос.")
  @NotNull
  public BigDecimal getVoice() {
    return voice;
  }

  public void setVoice(BigDecimal voice) {
    this.voice = voice;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Vote vote = (Vote) o;
    return Objects.equals(this.nickname, vote.nickname) &&
        Objects.equals(this.voice, vote.voice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nickname, voice);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Vote {\n");
    
    sb.append("    nickname: ").append(toIndentedString(nickname)).append("\n");
    sb.append("    voice: ").append(toIndentedString(voice)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

