/*
 * Copyright 2013-2025 Real Logic Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.sbe.properties;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.footnotes.EnableFootnotes;
import net.jqwik.api.footnotes.Footnotes;
import uk.co.real_logic.sbe.json.JsonPrinter;
import uk.co.real_logic.sbe.properties.arbitraries.SbeArbitraries;
import org.agrona.concurrent.UnsafeBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import static uk.co.real_logic.sbe.properties.PropertyTestUtil.addSchemaAndInputMessageFootnotes;

public class JsonPropertyTest
{
    @Property
    @EnableFootnotes
    void shouldGenerateValidJson(
        @ForAll("encodedMessage") final SbeArbitraries.EncodedMessage encodedMessage,
        final Footnotes footnotes)
    {
        final StringBuilder output = new StringBuilder();
        final JsonPrinter printer = new JsonPrinter(encodedMessage.ir());
        printer.print(output, new UnsafeBuffer(encodedMessage.buffer()), 0);
        try
        {
            new JSONObject(output.toString());
        }
        catch (final JSONException e)
        {
            addSchemaAndInputMessageFootnotes(footnotes, encodedMessage);
            throw new AssertionError("Invalid JSON: " + output);
        }
    }

    @Provide
    Arbitrary<SbeArbitraries.EncodedMessage> encodedMessage()
    {
        final SbeArbitraries.CharGenerationConfig config =
            SbeArbitraries.CharGenerationConfig.unrestricted();
        return SbeArbitraries.encodedMessage(config);
    }
}
