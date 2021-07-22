package mil.af.abms.midas.config.security.log;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductController;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.TeamService;

@WebMvcTest({ProductController.class})
class HttpLoggerInterceptorTests extends ControllerTestHarness {


    @MockBean
    ProductService productService;
    @MockBean
    TagService tagService;
    @MockBean
    ProjectService projectService;
    @MockBean
    TeamService teamService;

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_log_large_request_body() throws Exception {
        when(productService.findById(1L)).thenReturn(new Product());

        mockMvc.perform(get("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(BIG_REQUEST_BODY))
        )
                .andExpect(status().isOk());
    }

    public static final String BIG_REQUEST_BODY = "{\n" +
            "  \"children\": [\n" +
            "      {\n" +
            "        \"children\": [\n" +
            "            {\n" +
            "                \"children\": [\n" +
            "                ],\n" +
            "                \"productId\": 3,\n" +
            "                \"text\": \"my awesome strat5ajksfasjdhfakjsfhalskjfhasljfhaslkfhalskjfhalksjfhalksjfhaskljfhalksjdfhalskjdfhalskjfhalksjfhalksjfhalksjfhalksjfhalksjfhaklsjfhalksjfhalksjfhalksjfhlakjsfhalksjfhalksjfhalksjfhalksjfhalksjfhalksjfhalsfhalskfhalksjfhalksfhalksjfhalskjfhalksjfhalksfhaslkjfhaslkfhalskfhalskfhalskjfhalksjfhaskljfhalskfhalkjsfhlaksjfhlakjsfhlkajsfhlakjsjfhalkjsfh\",\n" +
            "                \"type\": \"STRATEGY\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"children\": [\n" +
            "                ],\n" +
            "                \"productId\": 3,\n" +
            "                \"text\": \"my awesome strat5ajksfasjdhfakjsfhalskjfhasljfhaslkfhalskjfhalksjfhalksjfhaskljfhalksjdfhalskjdfhalskjfhalksjfhalksjfhalksjfhalksjfhalksjfhaklsjfhalksjfhalksjfhalksjfhlakjsfhalksjfhalksjfhalksjfhalksjfhalksjfhalksjfhalsfhalskfhalksjfhalksfhalksjfhalskjfhalksjfhalksfhaslkjfhaslkfhalskfhalskfhalskjfhalksjfhaskljfhalskfhalkjsfhlaksjfhlakjsfhlkajsfhlakjsjfhalkjsfh\",\n" +
            "                \"type\": \"STRATEGY\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"children\": [\n" +
            "                ],\n" +
            "                \"productId\": 3,\n" +
            "                \"text\": \"my awesome strat5ajksfasjdhfakjsfhalskjfhasljfhaslkfhalskjfhalksjfhalksjfhaskljfhalksjdfhalskjdfhalskjfhalksjfhalksjfhalksjfhalksjfhalksjfhaklsjfhalksjfhalksjfhalksjfhlakjsfhalksjfhalksjfhalksjfhalksjfhalksjfhalksjfhalsfhalskfhalksjfhalksfhalksjfhalskjfhalksjfhalksfhaslkjfhaslkfhalskfhalskfhalskjfhalksjfhaskljfhalskfhalkjsfhlaksjfhlakjsfhlkajsfhlakjsjfhalkjsfh\",\n" +
            "                \"type\": \"STRATEGY\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"children\": [\n" +
            "                ],\n" +
            "                \"productId\": 3,\n" +
            "                \"text\": \"my awesome strat5ajksfasjdhfakjsfhalskjfhasljfhaslkfhalskjfhalksjfhalksjfhaskljfhalksjdfhalskjdfhalskjfhalksjfhalksjfhalksjfhalksjfhalksjfhaklsjfhalksjfhalksjfhalksjfhlakjsfhalksjfhalksjfhalksjfhalksjfhalksjfhalksjfhalsfhalskfhalksjfhalksfhalksjfhalskjfhalksjfhalksfhaslkjfhaslkfhalskfhalskfhalskjfhalksjfhaskljfhalskfhalkjsfhlaksjfhlakjsfhlkajsfhlakjsjfhalkjsfh\",\n" +
            "                \"type\": \"STRATEGY\"\n" +
            "            },\n" +
            "           {\n" +
            "                \"children\": [\n" +
            "                ],\n" +
            "                \"productId\": 3,\n" +
            "                \"text\": \"my awesome strat5ajksfasjdhfakjsfhalskjfhasljfhaslkfhalskjfhalksjfhalksjfhaskljfhalksjdfhalskjdfhalskjfhalksjfhalksjfhalksjfhalksjfhalksjfhaklsjfhalksjfhalksjfhalksjfhlakjsfhalksjfhalksjfhalksjfhalksjfhalksjfhalksjfhalsfhalskfhalksjfhalksfhalksjfhalskjfhalksjfhalksfhaslkjfhaslkfhalskfhalskfhalskjfhalksjfhaskljfhalskfhalkjsfhlaksjfhlakjsfhlkajsfhlakjsjfhalkjsfh\",\n" +
            "                \"type\": \"STRATEGY\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"children\": [\n" +
            "                ],\n" +
            "                \"productId\": 3,\n" +
            "                \"text\": \"my awesome strat5ajksfasjdhfakjsfhalskjfhasljfhaslkfhalskjfhalksjfhalksjfhaskljfhalksjdfhalskjdfhalskjfhalksjfhalksjfhalksjfhalksjfhalksjfhaklsjfhalksjfhalksjfhalksjfhlakjsfhalksjfhalksjfhalksjfhalksjfhalksjfhalksjfhalsfhalskfhalksjfhalksfhalksjfhalskjfhalksjfhalksfhaslkjfhaslkfhalskfhalskfhalskjfhalksjfhaskljfhalskfhalkjsfhlaksjfhlakjsfhlkajsfhlakjsjfhalkjsfh\",\n" +
            "                \"type\": \"STRATEGY\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"children\": [\n" +
            "                ],\n" +
            "                \"productId\": 3,\n" +
            "                \"text\": \"my awesome strat5ajksfasjdhfakjsfhalskjfhasljfhaslkfhalskjfhalksjfhalksjfhaskljfhalksjdfhalskjdfhalskjfhalksjfhalksjfhalksjfhalksjfhalksjfhaklsjfhalksjfhalksjfhalksjfhlakjsfhalksjfhalksjfhalksjfhalksjfhalksjfhalksjfhalsfhalskfhalksjfhalksfhalksjfhalskjfhalksjfhalksfhaslkjfhaslkfhalskfhalskfhalskjfhalksjfhaskljfhalskfhalkjsfhlaksjfhlakjsfhlkajsfhlakjsjfhalkjsfh\",\n" +
            "                \"type\": \"STRATEGY\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"children\": [\n" +
            "                ],\n" +
            "                \"productId\": 3,\n" +
            "                \"text\": \"my awesome strat6\",\n" +
            "                \"type\": \"STRATEGY\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"productId\": 3,\n" +
            "        \"text\": \"u4\",\n" +
            "        \"type\": \"GOAL\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"productId\": 3,\n" +
            "  \"text\": \"stonks\",\n" +
            "  \"status\": \"STARTED\"\n" +
            "}";

}
