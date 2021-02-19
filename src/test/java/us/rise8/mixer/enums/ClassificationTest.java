package us.rise8.mixer.enums;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

public class ClassificationTest {

    @Test
    public void shouldReturnExpectedEnumUNCLASS() {
        assertThat(Classification.UNCLASS.getName()).isEqualTo("UNCLASSIFIED");
        assertThat(Classification.UNCLASS.getBackgroundColor()).isEqualTo("#5bad76");
        assertThat(Classification.UNCLASS.getTextColor()).isEqualTo("#FFFFFF");
    }

    @Test
    public void shouldReturnExpectedEnumCUI() {
        assertThat(Classification.CUI.getName()).isEqualTo("CUI");
        assertThat(Classification.CUI.getBackgroundColor()).isEqualTo("#2849b8");
        assertThat(Classification.CUI.getTextColor()).isEqualTo("#FFFFFF");
    }

    @Test
    public void shouldReturnExpectedEnumSECRET() {
        assertThat(Classification.SECRET.getName()).isEqualTo("SECRET");
        assertThat(Classification.SECRET.getBackgroundColor()).isEqualTo("#be4242");
        assertThat(Classification.SECRET.getTextColor()).isEqualTo("#FFFFFF");
    }

    @Test
    public void shouldReturnExpectedEnumSCI() {
        assertThat(Classification.SCI.getName()).isEqualTo("SCI");
        assertThat(Classification.SCI.getBackgroundColor()).isEqualTo("#eff01a");
        assertThat(Classification.SCI.getTextColor()).isEqualTo("#000000");
    }
}
